package pl.com.seremak.simplebills.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;


@Document
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction extends VersionedEntity {

    public enum Type {
        INCOME, EXPENSE
    }

    @Pattern(regexp = "^[a-zA-Z]+\\w{1,19}", message = "Username must start with a letter and has 2 - 20 word characters (digits, letters, _)")
    private String user;

    private Integer transactionNumber;

    @NotNull(message = "Transaction type cannot be null")
    private Type type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Field(targetType = FieldType.DATE_TIME)
    private Instant date;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Category cannot be blank")
    private String category;
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount cannot be negative")
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

}
