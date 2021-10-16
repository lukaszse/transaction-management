package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import pl.com.seremak.simplebills.service.util.OperationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillCrudService {

    public static final String OPERATION_ERROR_MESSAGE = "Cannot {} Bill with id={}. Error={}";
    public static final String OPERATION_ERROR_MESSAGE_CATEGORY = "Cannot {} Bills with category={}. Error={}";
    public static final String OPERATION_ERROR_MESSAGE_ALL = "Cannot {} Bills. Error={}";

    private final BillCrudRepository billCrudRepository;

    public Mono<Bill> findBillById(final String id) {
        return billCrudRepository.findById(id)
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, id, error.getMessage()));
    }

    public Mono<List<Bill>> findAllBills() {
        return billCrudRepository.findAll()
                .collectList()
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE_ALL, OperationType.READ, error.getMessage()));
    }

    public Mono<List<Bill>> findBillsByCategory(final String category) {
        return billCrudRepository.findBillsByCategory(category)
                .collectList()
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE_CATEGORY, OperationType.READ, category, error.getMessage()));
    }

    public Mono<String> deleteBillById(final String id) {
        return billCrudRepository.deleteById(id)
                .map(bill -> id)
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, id, error.getMessage()));
    }
}
