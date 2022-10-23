package pl.com.seremak.simplebills.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.seremak.simplebills.model.User;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {

    private String userName;
    private String firstName;
    private String lastName;
    private BigDecimal sum;
    private BigDecimal mean;
    private LocalDate periodFrom;
    private LocalDate periodTo;

    public static UserInfoDto of(Tuple2<User, StatisticsDto> userAndStatistics) {
        return UserInfoDto.builder()
                .userName(userAndStatistics.getT1().getLogin())
                .firstName(userAndStatistics.getT1().getFirstName())
                .lastName(userAndStatistics.getT1().getLastName())
                .lastName(userAndStatistics.getT1().getLastName())
                .sum(userAndStatistics.getT2().getSum())
                .mean(userAndStatistics.getT2().getMean())
                .periodFrom(userAndStatistics.getT2().getPeriodFrom())
                .periodTo(userAndStatistics.getT2().getPeriodTo())
                .build();
    }
}
