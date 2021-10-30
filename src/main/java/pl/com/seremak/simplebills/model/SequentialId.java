package pl.com.seremak.simplebills.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class SequentialId {

    @Id
    private String user;
    private int sequentialId;
}
