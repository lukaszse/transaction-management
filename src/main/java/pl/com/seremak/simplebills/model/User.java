package pl.com.seremak.simplebills.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @NotBlank(message = "Email cannot be blank")
    private String login;
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
