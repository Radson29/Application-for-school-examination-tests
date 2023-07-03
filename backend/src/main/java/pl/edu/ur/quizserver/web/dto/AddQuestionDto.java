package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AddQuestionDto {
    @NotBlank
    @Size(min = 4)
    private String value;

    @NotNull
    @Size(min = 3, max = 6)
    @Valid
    private List<AddAnswerDto> answers;
}
