package pl.com.seremak.simplebills.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SequentialId {

    @Id
    private String user;
    private int sequentialId;
}
