package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.endpoint.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    public static final String CATEGORY_FIELD = "category";
    public static final String DATE_FIELD = "date";
    public static final String USER_FIELD = "user";
    private final BillCrudRepository crudRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<BigDecimal> calculateSumForUserAndCategory(final String userName, final BillQueryParams params) {
        return mongoTemplate.find(
                        prepareFindBillByUserAndCategoryQuery(userName, params),
                        Bill.class)
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Mono<BigDecimal> calculateMeanForUserAndCategory(final String userName, final BillQueryParams params) {
        return calculateSumForUserAndCategory(userName, params)
                .zipWith(countByUserAndCategory(userName, params.getCategory()))
                .map(tuple -> tuple.getT1().divide(tuple.getT2(), 2, RoundingMode.HALF_UP));
    }

    public Mono<BigDecimal> countByUserAndCategory(final String userName, final String category) {
        return crudRepository.countByUserAndCategory(userName, category)
                .map(BigDecimal::valueOf);
    }

    @SuppressWarnings("unchecked")
    private Query prepareFindBillByUserAndCategoryQuery(final String userName, final BillQueryParams params) {
        Query query = new Query().addCriteria(Criteria.where(USER_FIELD).is(userName));
        if (params.getCategory() != null) query.addCriteria(Criteria.where(CATEGORY_FIELD).is(params.getCategory()));
        return addBetweenDatesCriteria(params, query);
    }

    private Query addBetweenDatesCriteria(final BillQueryParams params, final Query query) {
        Criteria criteria = Criteria.where(DATE_FIELD);
        if (params.getDateFrom() != null && params.getDateTo() != null) {
            criteria.gte(params.getDateFrom()).lte(params.getDateTo());
            query.addCriteria(criteria);
        } else if (params.getDateFrom() != null) {
            criteria.gte(params.getDateFrom());
            query.addCriteria(criteria);
        } else if (params.getDateTo() != null) {
            criteria.lte(params.getDateTo());
            query.addCriteria(criteria);
        }
        return query;
    }
}
