package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserDetailsDto {

    @NotBlank
    @Size(max = 15)
    private String firstName;

    @NotBlank
    @Size(max = 15)
    private String lastName;

    @NotBlank
    @Size(min = 11, max = 11)
    private String pesel;

    @NotBlank
    @Size(min = 4, max = 15)
    private String login;
}
