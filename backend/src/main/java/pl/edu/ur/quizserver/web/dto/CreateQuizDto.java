package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CreateQuizDto {
    @NotNull
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    @Size(min = 1)
    private String description;

    @NotNull
    @Min(1)
    private short quizTime;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<QGRecipeDto> recipes;
}
