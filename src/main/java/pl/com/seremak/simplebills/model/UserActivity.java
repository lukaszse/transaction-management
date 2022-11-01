package pl.com.seremak.simplebills.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserActivity extends VersionedEntity {

    public enum Activity {
        LOGGING_IN, LOGGING_OUT
    }

    private String username;
    private Activity activity;
}
