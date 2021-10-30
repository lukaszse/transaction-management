package pl.com.seremak.simplebills.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.exceptions.NotFoundException;
import pl.com.seremak.simplebills.model.bill.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import pl.com.seremak.simplebills.repository.SequentialIdRepository;
import pl.com.seremak.simplebills.service.util.OperationType;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillCrudService {

    public static final String OPERATION_ERROR_MESSAGE = "Cannot {} Bill with id={}. Error={}";
    public static final String OPERATION_ERROR_MESSAGE_CATEGORY = "Cannot {} Bills with category={}. Error={}";
    public static final String OPERATION_ERROR_MESSAGE_ALL = "Cannot {} Bills. Error={}";
    public static final String DEFAULT_USER = "default_user";
    public static final String NOT_FOUND_ERROR_MESSAGE = "Bill with id=%s not found";

    private final BillCrudRepository crudRepository;
    private final SequentialIdRepository sequentialIdRepository;

    public Mono<String> createBill(final Bill bill) {
        return sequentialIdRepository.generateId(DEFAULT_USER)
                .map(id -> updateBillId(bill, id))
                .map(theBill -> crudRepository.save(bill)
                        .map(Bill::getId))
                .flatMap(id -> id);

    }

    public Mono<Bill> findBillById(final String id) {
        return crudRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(id))))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, id, error.getMessage()));
    }

    public Mono<List<Bill>> findAllBills() {
        return crudRepository.findAll()
                .collectList()
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE_ALL, OperationType.READ, error.getMessage()));
    }

    public Mono<List<Bill>> findBillsByCategory(final String category) {
        return crudRepository.findBillsByCategory(category)
                .collectList()
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE_CATEGORY, OperationType.READ, category, error.getMessage()));
    }

    public Mono<String> deleteBillById(final String id) {
        return crudRepository.deleteById(id)
                .map(bill -> id)
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, id, error.getMessage()));
    }

    private Bill updateBillId(final Bill bill, final String id) {
        bill.setId(id);
        return bill;
    }
}
