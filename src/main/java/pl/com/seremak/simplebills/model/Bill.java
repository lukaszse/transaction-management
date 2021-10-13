package pl.com.seremak.simplebills.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Builder
public class Bill {

    @Id
    public int id;
    public String description;
    public String category;

    public Bill(int id, String description, String category) {
        this.id = id;
        this.description = description;
        this.category = category;
    }
}
