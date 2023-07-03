package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
@NoArgsConstructor
public class QGRecipeDto {
    @NotNull
    private Long groupId;

    @NotNull
    @Min(0)
    private Integer count;
}
