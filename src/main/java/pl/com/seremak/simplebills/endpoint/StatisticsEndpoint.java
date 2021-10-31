package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.com.seremak.simplebills.service.StatisticsService;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsEndpoint {

    private final StatisticsService service;

    @GetMapping("/sum")
    public Mono<ResponseEntity<BigDecimal>> calculateSum(final Mono<Principal> principal, BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.calculateSumForUserAndCategory(userName, params))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/mean")
    public Mono<ResponseEntity<BigDecimal>> calculateMean(final Mono<Principal> principal, BillQueryParams params) {
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.calculateMeanForUserAndCategory(userName,params))
                .map(ResponseEntity::ok);
    }
}
