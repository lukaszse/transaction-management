package pl.com.seremak.simplebills.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.exceptions.NotFoundException;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import pl.com.seremak.simplebills.repository.BillSearchRepository;
import pl.com.seremak.simplebills.util.OperationType;
import pl.com.seremak.simplebills.util.VersionedEntityUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDate;
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
                .map(BillService::setCurrentDateIfMissing)
                .map(VersionedEntityUtils::setMetadata)
                .flatMap(crudRepository::save);
    }

    public Mono<Bill> findBillByBillNumber(final String username, final String billNumber) {
        return crudRepository.findByUserAndBillNumber(username, billNumber)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(billNumber))))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, billNumber, username, error.getMessage()));
    }

    public Mono<Tuple2<List<Bill>, Long>> findBillsByCategory(final String username, final BillQueryParams params) {
        return billSearchRepository.find(username, params)
                .collectList()
                .zipWith(countBillsByCategory(username, params));
    }

    public Mono<Long> countBillsByCategory(final String username, final BillQueryParams params) {
        return billSearchRepository.count(username, params);
    }

    public Mono<Bill> deleteBillByBillNumber(final String username, final String billNumber) {
        return crudRepository.deleteByUserAndBillNumber(username, billNumber)
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, billNumber, username, error.getMessage()));
    }

    public Mono<Bill> updateBillNumber(final String username, final Bill bill) {
        return billSearchRepository.updateBillNumber(username, bill)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(bill.getBillNumber()))));
    }

    private static Bill setBillNumber(final Bill bill, final Integer id, final String username) {
        bill.setUser(username);
        bill.setBillNumber(id);
        return bill;
    }

    private static Bill setCurrentDateIfMissing(final Bill bill) {
        if (bill.getDate() == null) {
            bill.setDate(LocalDate.now());
        }
        return bill;
    }
}
