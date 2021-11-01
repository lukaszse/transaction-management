package pl.com.seremak.simplebills.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordDto {

    @NotBlank
    private String user;
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
