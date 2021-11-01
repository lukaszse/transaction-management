package pl.com.seremak.simplebills.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.model.Bill;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BillCrudRepository extends ReactiveCrudRepository<Bill, String> {

    Flux<Bill> findByUserAndCategory(final String user, final String category);

    Mono<Bill> deleteByUserAndBillNumber(final String user, final String billNumber);

    Mono<Bill> findByUserAndBillNumber(final String user, final String billNumber);

    Mono<Long> countByUserAndCategory(final String user, final String category);

    Mono<Long> countByUser(final String user);
}
