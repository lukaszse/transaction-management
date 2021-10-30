package pl.com.seremak.simplebills.model.bill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.com.seremak.simplebills.model.Metadata;

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
    private String description;
    private String category;
    private Metadata metadata;
}
