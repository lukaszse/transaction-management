package pl.com.seremak.simplebills.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.model.Bill;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BillCrudRepository extends ReactiveCrudRepository<Bill, String> {

    Flux<Bill> findBillsByCategory(final String category);
    Flux<Bill> findBillByUserAndCategory(final String user, final String category);
    Mono<Bill> deleteBillByUserAndId(final String user, final String category);
    Mono<Bill> findBillByUserAndId(final String user, final String id);
}
