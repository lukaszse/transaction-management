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

    public static final String OPERATION_ERROR_MESSAGE = "Cannot {} Bill with id={} for user={}. Error={}";
    public static final String OPERATION_ERROR_MESSAGE_CATEGORY = "Cannot {} Bills with category={}. Error={}";
    public static final String OPERATION_ERROR_MESSAGE_ALL = "Cannot {} Bills. Error={}";
    public static final String DEFAULT_USER = "default_user";
    public static final String NOT_FOUND_ERROR_MESSAGE = "Bill with id=%s not found";
    private static final String USER_FIELD = "user";

    private final BillCrudRepository crudRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final SequentialIdService sequentialIdRepository;
    private final ObjectMapper objectMapper;

    public Mono<String> createBill(final String userName, final Bill bill) {
        return sequentialIdRepository.generateId(DEFAULT_USER)
                .map(id -> updateBillId(bill, id, userName))
                .map(this::setCurrentDateIfMissing)
                .map(this::setMetadata)
                .map(theBill -> crudRepository.save(theBill)
                        .map(Bill::getId))
                .flatMap(id -> id);
    }

    public Mono<Bill> findBillByIdForUser(final String userName, final String id) {
        return crudRepository.findBillByUserAndId(userName, id)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(id))))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, id, userName, error.getMessage()));
    }

    public Mono<List<Bill>> findAllBills() {
        return crudRepository.findAll()
                .collectList()
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE_ALL, OperationType.READ, error.getMessage()));
    }

    public Mono<List<Bill>> findBillsByCategoryForUser(final String userName, final String category) {
        return crudRepository.findBillByUserAndCategory(userName, category)
                .collectList()
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE_CATEGORY, OperationType.READ, category, error.getMessage()));
    }

    public Mono<String> deleteBillByIdForUser(final String userName, final String id) {
        return crudRepository.deleteBillByUserAndId(userName, id)
                .map(bill -> id)
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, id, userName, error.getMessage()));
    }

    public Mono<String> updateBillByIdForUser(final String userName, final Bill bill) {
        return mongoTemplate.findAndModify(
                        prepareFindBillQuery(userName, bill.getId()),
                        preparePartialUpdateQuery(bill),
                        new FindAndModifyOptions().returnNew(true),
                        Bill.class)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(bill.getId()))))
                .map(Bill::getId);
    }

    private Bill updateBillId(final Bill bill, final String id, final String userName) {
        bill.setUser(userName);
        bill.setId(id);
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

    private Query prepareFindBillQuery(final String user, final String id) {
        return new Query()
                .addCriteria(Criteria.where(USER_FIELD).is(user))
                .addCriteria(Criteria.where(ID_FIELD).is(id));
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
