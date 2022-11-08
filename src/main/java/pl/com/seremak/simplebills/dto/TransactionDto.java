package pl.com.seremak.simplebills.dto;

import com.mongodb.lang.Nullable;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class TransactionDto {

    private Integer transactionNumber;

    @Pattern(regexp = "^[a-zA-Z]+\\w{1,19}", message = "Login must start with a letter and has 2 - 20 word characters (digits, letters, _)")
    private String user;

    @Nullable
    private LocalDate date;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount cannot be negative")
    private BigDecimal amount;
}
