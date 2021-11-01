package pl.com.seremak.simplebills.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {

    private String userName;
    private String category;
    private BigDecimal sum;
    private BigDecimal mean;
    private Instant periodFrom;
    private Instant periodTo;

    public static StatisticsDto of(final Tuple2<BigDecimal, BigDecimal> sumAndmeanTuple,
                                   final String userName,
                                   final String category,
                                   final Instant periodFrom,
                                   final Instant periodTo) {
        return StatisticsDto.builder()
                .userName(userName)
                .category(category)
                .sum(sumAndmeanTuple.getT1())
                .mean(sumAndmeanTuple.getT2())
                .periodFrom(periodFrom)
                .periodTo(periodTo)
                .build();
    }
}
