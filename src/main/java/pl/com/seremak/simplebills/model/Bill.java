package pl.com.seremak.simplebills.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;


@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    private String id;
    private String user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant date;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Category cannot be blank")
    private String category;
    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be negative")
    private BigDecimal amount;
    private Metadata metadata;
}
