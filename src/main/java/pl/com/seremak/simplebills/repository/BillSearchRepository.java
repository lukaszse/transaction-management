package pl.com.seremak.simplebills.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;

import static pl.com.seremak.simplebills.util.MongoQueryUtils.*;

@Repository
@RequiredArgsConstructor
public class BillSearchRepository {

    private static final String USER_FIELD = "user";
    private static final String BILL_NUMBER_FIELD = "billNumber";

    private final ReactiveMongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public Flux<Bill> find(final String username, final BillQueryParams params) {
        return mongoTemplate.find(
                        prepareFindBillByUserAndCategoryQueryPageable(username, params),
                        Bill.class);
    }

    public Mono<Long> count(final String username, final BillQueryParams params) {
        return mongoTemplate.count(
                prepareFindBillByUserAndCategoryQuery(username, params),
                Bill.class);
    }

    public Mono<Bill> updateBillNumber(final String username, final Bill bill) {
        return mongoTemplate.findAndModify(
                        prepareFindBillQuery(username, bill.getBillNumber()),
                        preparePartialUpdateQuery(bill),
                        new FindAndModifyOptions().returnNew(true),
                        Bill.class);
    }

    private static Query prepareFindBillQuery(final String user, final String billNumber) {
        return new Query()
                .addCriteria(Criteria.where(USER_FIELD).is(user))
                .addCriteria(Criteria.where(BILL_NUMBER_FIELD).is(billNumber));
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
