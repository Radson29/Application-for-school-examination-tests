package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.ur.quizserver.validation.PasswordMatches;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@Getter
@Setter
@PasswordMatches
public class ChangePasswordDto {

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;
}