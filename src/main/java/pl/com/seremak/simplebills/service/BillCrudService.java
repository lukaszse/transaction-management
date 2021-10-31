package pl.com.seremak.simplebills.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.endpoint.dto.BillQueryParams;
import pl.com.seremak.simplebills.exceptions.NotFoundException;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.model.Metadata;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import pl.com.seremak.simplebills.service.util.OperationType;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static pl.com.seremak.simplebills.service.util.ServiceCommons.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillCrudService {

    public static final String OPERATION_ERROR_MESSAGE = "Cannot {} bill with billNumber={} for user={}. Error={}";
    public static final String OPERATION_ERROR_MESSAGE_CATEGORY = "Cannot {} bills with category={}. Error={}";
    public static final String NOT_FOUND_ERROR_MESSAGE = "Bill with billNumber=%s not found.";
    private static final String USER_FIELD = "user";
    public static final String BILL_NUMBER_FIELD = "billNumber";

    private final BillCrudRepository crudRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final SequentialIdService sequentialIdRepository;
    private final ObjectMapper objectMapper;

    public Mono<String> createBill(final String userName, final Bill bill) {
        return sequentialIdRepository.generateId(userName)
                .map(id -> setBillNumber(bill, id, userName))
                .map(this::setCurrentDateIfMissing)
                .map(this::setMetadata)
                .flatMap(theBill -> crudRepository.save(theBill)
                        .map(Bill::getBillNumber));
    }

    public Mono<Bill> findBillByBillNumberForUser(final String userName, final String billNumber) {
        return crudRepository.findByUserAndBillNumber(userName, billNumber)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(billNumber))))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, billNumber, userName, error.getMessage()));
    }

    public Mono<List<Bill>> findBillsByCategoryForUser(final String userName, final BillQueryParams params) {
        return mongoTemplate.find(
                        prepareFindBillByUserAndCategoryQuery(userName, params),
                        Bill.class)
                .collectList();
    }

    public Mono<String> deleteBillByBillNumberForUser(final String userName, final String billNumber) {
        return crudRepository.deleteByUserAndBillNumber(userName, billNumber)
                .map(bill -> billNumber)
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, billNumber, userName, error.getMessage()));
    }

    public Mono<String> updateBillByBillNumberForUser(final String userName, final Bill bill) {
        return mongoTemplate.findAndModify(
                        prepareFindBillQuery(userName, bill.getBillNumber()),
                        preparePartialUpdateQuery(bill),
                        new FindAndModifyOptions().returnNew(true),
                        Bill.class)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(bill.getBillNumber()))))
                .map(Bill::getBillNumber);
    }

    private Bill setBillNumber(final Bill bill, final String id, final String userName) {
        bill.setUser(userName);
        bill.setBillNumber(id);
        return bill;
    }

    private Bill setCurrentDateIfMissing(final Bill bill) {
        if (bill.getDate() == null) {
            bill.setDate(Instant.now());
        }
        return bill;
    }

    private Bill setMetadata(final Bill bill) {
        bill.setMetadata(
                Metadata.builder()
                        .createdAt(Instant.now())
                        .modifiedAt(Instant.now())
                        .version(1L)
                        .build());
        return bill;
    }

    private Query prepareFindBillQuery(final String user, final String billNumber) {
        return new Query()
                .addCriteria(Criteria.where(USER_FIELD).is(user))
                .addCriteria(Criteria.where(BILL_NUMBER_FIELD).is(billNumber));
    }

    @SuppressWarnings({"unchecked"})
    private Update preparePartialUpdateQuery(final Bill bill) {
        Update update = new Update();
        Map<String, Object> fieldsMap = objectMapper.convertValue(bill, Map.class);
        fieldsMap.entrySet().stream()
                .filter(field -> field.getValue() != null)
                .forEach(field -> update.set(field.getKey(), field.getValue()));
        return updateMetadata(update);
    }
}
