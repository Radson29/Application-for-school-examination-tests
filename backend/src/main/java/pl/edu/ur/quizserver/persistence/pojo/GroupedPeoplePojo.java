package pl.edu.ur.quizserver.persistence.pojo;

import lombok.*;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonGroupEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupedPeoplePojo {
    private PersonGroupEntity group;
    private List<PersonEntity> people;
}
