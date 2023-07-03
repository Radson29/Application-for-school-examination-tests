package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AnsweredQuestionDto {

    @NotNull
    private Long questionId;

    @NotNull
    @Valid
    private List<Long> answersIds = new ArrayList<>();
}

