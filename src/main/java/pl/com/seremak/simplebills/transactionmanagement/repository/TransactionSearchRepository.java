package pl.com.seremak.simplebills.transactionmanagement.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.commons.dto.http.TransactionQueryParams;
import pl.com.seremak.simplebills.commons.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static pl.com.seremak.simplebills.commons.utils.MongoQueryHelper.preparePartialUpdateQuery;
import static pl.com.seremak.simplebills.commons.utils.TransactionQueryUtils.prepareFindByCategoryQuery;
import static pl.com.seremak.simplebills.commons.utils.TransactionQueryUtils.prepareFindByCategoryQueryPageable;

@Repository
@RequiredArgsConstructor
public class TransactionSearchRepository {

    private final ReactiveMongoTemplate mongoTemplate;


    public Flux<Transaction> find(final String username, final TransactionQueryParams params) {
        return mongoTemplate.find(
                prepareFindByCategoryQueryPageable(username, params),
                Transaction.class);
    }

    public Mono<Long> count(final String username, final TransactionQueryParams params) {
        return mongoTemplate.count(
                prepareFindByCategoryQuery(username, params),
                Transaction.class);
    }

    public Mono<Transaction> updateTransaction(final Transaction transaction) {
        return mongoTemplate.findAndModify(
                prepareFindTransactionQuery(transaction.getUser(), transaction.getTransactionNumber()),
                preparePartialUpdateQuery(transaction, Transaction.class),
                new FindAndModifyOptions().returnNew(true),
                Transaction.class);
    }

    private static Query prepareFindTransactionQuery(final String user, final Integer transactionNumber) {
        return new Query()
                .addCriteria(Criteria.where("user").is(user))
                .addCriteria(Criteria.where("transactionNumber").is(transactionNumber));
    }
}
