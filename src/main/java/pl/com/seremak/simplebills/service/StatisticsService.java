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

    public Mono<BigDecimal> calculateSumForUserAndCategory(final String userName, final String category) {
        return crudRepository.findBillByUserAndCategory(userName, category)
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Mono<BigDecimal> calculateMeanForUserAndCategory(final String userName, final String category) {
        return calculateSumForUserAndCategory(userName, category)
                .zipWith(countByUserAndCategory(userName, category))
                .map(tuple -> tuple.getT1().divide(tuple.getT2(), 2, RoundingMode.HALF_UP));
    }

    public Mono<BigDecimal> countByUserAndCategory(final String userName, final String category) {
        return crudRepository.countByUserAndCategory(userName, category)
                .map(BigDecimal::valueOf);
    }

}
