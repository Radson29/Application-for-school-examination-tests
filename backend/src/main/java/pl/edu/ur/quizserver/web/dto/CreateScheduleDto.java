package pl.edu.ur.quizserver.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CreateScheduleDto {

    @NotNull
    private Long quizId;

    @Future
    @NotNull
    private Date startsAt;

    @Future
    @NotNull
    private Date endsAt;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<Integer> students;
}
