package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.endpoint.dto.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static pl.com.seremak.simplebills.service.util.ServiceCommons.prepareFindBillByUserAndCategoryQuery;

@Service
@RequiredArgsConstructor
public class StatisticsService {


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
}
