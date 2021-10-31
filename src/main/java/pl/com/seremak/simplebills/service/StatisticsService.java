package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BillCrudRepository crudRepository;

    public Mono<BigDecimal> calculateSumForCategory(final String category) {
        return crudRepository.findBillsByCategory(category)
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Mono<BigDecimal> calculateMeanForCategory(final String category) {
        return calculateSumForCategory(category)
                .zipWith(count(category))
                .map(tuple -> tuple.getT1().divide(tuple.getT2(), 2, RoundingMode.HALF_UP));
    }

    public Mono<BigDecimal> count(final String category) {
        return crudRepository.count()
                .map(BigDecimal::valueOf);
    }

}
