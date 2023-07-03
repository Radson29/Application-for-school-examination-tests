package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Data
@NoArgsConstructor
public class CreateGroupDto {
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;
}