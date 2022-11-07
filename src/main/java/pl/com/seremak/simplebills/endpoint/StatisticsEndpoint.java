package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.dto.StatisticsDto;
import pl.com.seremak.simplebills.service.StatisticsService;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsEndpoint {

    public static final String STATISTICS_FETCHED_MESSAGE = "Statistics for user={} successfully fetched";
    public static final String STATISTICS_REQUEST_MESSAGE = "Request for statistics for user={} received.";
    public static final String CALCULATE_MEAN_REQUEST_MESSAGE = "Request for mean calculation for user={} received.";
    public static final String CALCULATE_SUM_REQUEST_MESSAGE = "Request for mean calculation for user={} received.";

    private final StatisticsService service;

    @GetMapping(value = "/sum", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BigDecimal>> calculateSum(final Mono<Principal> principal, final BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .doOnEach(userName -> log.info(CALCULATE_SUM_REQUEST_MESSAGE, userName))
                .flatMap(userName -> service.calculateSumForUserAndCategory(userName, params))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/mean", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BigDecimal>> calculateMean(final Mono<Principal> principal, final BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .doOnEach(userName -> log.info(CALCULATE_MEAN_REQUEST_MESSAGE, userName))
                .flatMap(userName -> service.calculateMeanForUserAndCategory(userName, params))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/user-statistics", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<StatisticsDto>> getStatisticsForUser(final Mono<Principal> principal, final BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .doOnEach(userName -> log.info(STATISTICS_REQUEST_MESSAGE, userName))
                .flatMap(userName -> service.getStatisticsForUser(userName, params))
                .doOnSuccess(statisticsDto -> log.info(STATISTICS_FETCHED_MESSAGE, statisticsDto.getUserName()))
                .map(ResponseEntity::ok);
    }
}
