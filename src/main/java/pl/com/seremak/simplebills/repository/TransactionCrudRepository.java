package pl.com.seremak.simplebills.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.commons.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionCrudRepository extends ReactiveCrudRepository<Transaction, String> {

    Flux<Transaction> findByUserAndCategory(final String user, final String category);

    Mono<Transaction> deleteByUserAndTransactionNumber(final String user, final Integer transactionNumber);

    Mono<Transaction> findByUserAndTransactionNumber(final String user, final Integer transactionNumber);
}
