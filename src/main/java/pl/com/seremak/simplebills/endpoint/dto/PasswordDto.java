package pl.com.seremak.simplebills.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDto {

    @NotBlank
    private String user;
    @NotBlank
    private String password;
}
