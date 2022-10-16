package pl.com.seremak.simplebills.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.exceptions.NotFoundException;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.model.Metadata;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import pl.com.seremak.simplebills.repository.BillSearchRepository;
import pl.com.seremak.simplebills.util.OperationType;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillService {

    public static final String OPERATION_ERROR_MESSAGE = "Cannot {} bill with billNumber={} for user={}. Error={}";
    private static final String NOT_FOUND_ERROR_MESSAGE = "Bill with billNumber=%s not found.";


    private final BillCrudRepository crudRepository;
    private final SequentialIdService sequentialIdRepository;
    private final BillSearchRepository billSearchRepository;

    public Mono<Bill> createBill(final String username, final Bill bill) {
        return sequentialIdRepository.generateId(username)
                .map(id -> setBillNumber(bill, id, username))
                .map(this::setCurrentDateIfMissing)
                .map(this::setMetadata)
                .flatMap(crudRepository::save);
    }

    public Mono<Bill> findBillByBillNumberForUser(final String username, final String billNumber) {
        return crudRepository.findByUserAndBillNumber(username, billNumber)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(billNumber))))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, billNumber, username, error.getMessage()));
    }

    public Mono<Tuple2<List<Bill>, Long>> findBillsByCategoryForUser(final String username, final BillQueryParams params) {
        return billSearchRepository.find(username, params)
                .collectList()
                .zipWith(countBillsByCategoryForUser(username, params));
    }

    public Mono<Long> countBillsByCategoryForUser(final String username, final BillQueryParams params) {
        return billSearchRepository.count(username, params);
    }

    public Mono<Bill> deleteBillByBillNumberForUser(final String username, final String billNumber) {
        return crudRepository.deleteByUserAndBillNumber(username, billNumber)
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, billNumber, username, error.getMessage()));
    }

    public Mono<Bill> updateBillNumber(final String username, final Bill bill) {
        return billSearchRepository.updateBillNumber(username, bill)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(bill.getBillNumber()))));
    }

    private Bill setBillNumber(final Bill bill, final String id, final String username) {
        bill.setUser(username);
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
}
