package pl.com.seremak.simplebills.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordDto {

    @NotBlank
    private String user;
    @NotBlank
    private String oldPassword;
    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^\\w{5,20}", message = "Password must contain 6 - 20 word characters (digits, letters, _)")
    private String newPassword;
    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^\\w{5,20}", message = "Password must contain 6 - 20 word characters (digits, letters, _)")
    private String confirmNewPassword;
}
