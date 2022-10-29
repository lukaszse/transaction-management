package pl.com.seremak.simplebills.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;


@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bill {

    private String billNumber;
    @Pattern(regexp = "^[a-zA-Z]+\\w{1,19}", message = "Login must start with a letter and has 2 - 20 word characters (digits, letters, _)")
    private String user;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Category cannot be blank")
    private String category;
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount cannot be negative")
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;
    private Metadata metadata;
}
