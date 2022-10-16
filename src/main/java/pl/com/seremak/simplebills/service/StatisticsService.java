package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.dto.StatisticsDto;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static pl.com.seremak.simplebills.util.ServiceCommons.prepareFindBillByUserAndCategoryQuery;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {


    public static final String STATISTICS_FETCHING_ERROR = "Error while fetching statistics for user={} occured. Error={}";
    public static final String SUM_CALCULATION_ERROR = "Error while calculating sum for user={}, Error={}";
    public static final String MEAN_CALCULATION_ERROR = "Error while calculating mean for user={}, Error={}";
    private final BillCrudRepository crudRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<BigDecimal> calculateSumForUserAndCategory(final String userName, final BillQueryParams params) {
        return mongoTemplate.find(
                        prepareFindBillByUserAndCategoryQuery(userName, params),
                        Bill.class)
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doOnError(error -> log.error(SUM_CALCULATION_ERROR, userName, error.getMessage()));
    }

    public Mono<BigDecimal> calculateMeanForUserAndCategory(final String userName, final BillQueryParams params) {
        return calculateSumForUserAndCategory(userName, params)
                .zipWith(countByUserAndCategory(userName, params.getCategory()))
                .map(tuple -> tuple.getT1().divide(tuple.getT2(), 2, RoundingMode.HALF_UP))
                .onErrorReturn(ArithmeticException.class, BigDecimal.ZERO)
                .doOnError(error -> log.error(MEAN_CALCULATION_ERROR, userName, error.getMessage()));

    }

    public Mono<StatisticsDto> getStatisticsForUser(final String userName, final BillQueryParams params) {
        return calculateSumForUserAndCategory(userName, params)
                .zipWith(calculateMeanForUserAndCategory(userName, params))
                .map(tuple -> StatisticsDto.of(tuple,
                        userName,
                        params.getCategory(),
                        params.getDateFrom(),
                        params.getDateTo()))
                .doOnError(error ->
                        log.error(STATISTICS_FETCHING_ERROR, userName, error.getMessage()));
    }

    private Mono<BigDecimal> countByUserAndCategory(final String userName, final String category) {
        return category != null ?
                crudRepository.countByUserAndCategory(userName, category).map(BigDecimal::valueOf) :
                crudRepository.countByUser(userName).map(BigDecimal::valueOf);
    }
}
