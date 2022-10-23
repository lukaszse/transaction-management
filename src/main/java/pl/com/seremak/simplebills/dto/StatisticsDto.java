package pl.com.seremak.simplebills.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsDto {

    private String userName;
    private String category;
    private BigDecimal sum;
    private BigDecimal mean;
    private LocalDate periodFrom;
    private LocalDate periodTo;

    public static StatisticsDto of(final Tuple2<BigDecimal, BigDecimal> sumAndmeanTuple,
                                   final String userName,
                                   final String category,
                                   final LocalDate periodFrom,
                                   final LocalDate periodTo) {
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
