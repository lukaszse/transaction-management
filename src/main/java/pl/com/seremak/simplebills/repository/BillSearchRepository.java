package pl.com.seremak.simplebills.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static java.util.Objects.nonNull;
import static pl.com.seremak.simplebills.util.BillQueryUtils.prepareFindByCategoryQuery;
import static pl.com.seremak.simplebills.util.BillQueryUtils.prepareFindByCategoryQueryPageable;
import static pl.com.seremak.simplebills.util.ReflectionsUtils.getFieldValue;
import static pl.com.seremak.simplebills.util.VersionedEntityUtils.updateMetadata;

@Repository
@RequiredArgsConstructor
public class BillSearchRepository {

    private static final String USER_FIELD = "user";
    private static final String BILL_NUMBER_FIELD = "billNumber";
    private final ReactiveMongoTemplate mongoTemplate;


    public Flux<Bill> find(final String username, final BillQueryParams params) {
        return mongoTemplate.find(
                prepareFindByCategoryQueryPageable(username, params),
                Bill.class);
    }

    public Mono<Long> count(final String username, final BillQueryParams params) {
        return mongoTemplate.count(
                prepareFindByCategoryQuery(username, params),
                Bill.class);
    }

    public Mono<Bill> updateBill(final String username, final Bill bill) {
        return mongoTemplate.findAndModify(
                prepareFindBillQuery(username, bill.getBillNumber()),
                preparePartialUpdateQuery(bill),
                new FindAndModifyOptions().returnNew(true),
                Bill.class);
    }

    private static Query prepareFindBillQuery(final String user, final Integer billNumber) {
        return new Query()
                .addCriteria(Criteria.where(USER_FIELD).is(user))
                .addCriteria(Criteria.where(BILL_NUMBER_FIELD).is(billNumber));
    }

    private static Update preparePartialUpdateQuery(final Bill bill) {
        final Update update = new Update();
        Arrays.stream(Bill.class.getDeclaredFields())
                .filter(field -> nonNull(getFieldValue(field, bill)))
                .forEach(field -> update.set(field.getName(), getFieldValue(field, bill)));
        return updateMetadata(update);
    }
}
