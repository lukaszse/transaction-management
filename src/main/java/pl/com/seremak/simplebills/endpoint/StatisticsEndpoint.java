package pl.com.seremak.simplebills.endpoint;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.com.seremak.simplebills.service.StatisticsService;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsEndpoint {

    private final StatisticsService service;

    @GetMapping("/sum")
    Mono<ResponseEntity<BigDecimal>> calculateSum(@RequestParam final String category) {
        return service.calculateSumForCategory(category)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/mean")
    Mono<ResponseEntity<BigDecimal>> calculateMean(@RequestParam final String category) {
        return service.calculateMeanForCategory(category)
                .map(ResponseEntity::ok);
    }
}
