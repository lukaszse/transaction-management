package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsEndpoint {

    private final StatisticsService service;

    @GetMapping(value = "/sum", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BigDecimal>> calculateSum(final Mono<Principal> principal, BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.calculateSumForUserAndCategory(userName, params))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/mean", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BigDecimal>> calculateMean(final Mono<Principal> principal, BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.calculateMeanForUserAndCategory(userName, params))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/user-statistics", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<StatisticsDto>> getStatisticsForUser(final Mono<Principal> principal, BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.getStatisticsForUser(userName, params))
                .map(ResponseEntity::ok);
    }
}
