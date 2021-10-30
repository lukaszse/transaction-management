package pl.com.seremak.simplebills.model.bill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.com.seremak.simplebills.model.Metadata;

import javax.validation.constraints.NotBlank;
import java.time.Instant;


@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    private String id;
    private Instant date;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Category cannot be blank")
    private String category;
    private Metadata metadata;
}
