package pl.edu.ur.quizserver.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.ur.quizserver.web.util.Views;

import javax.persistence.*;

@JsonView(Views.QuizTeacherPublic.class)
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "qgrecipes")
public class QGRecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "quizId")
    private QuizEntity quiz;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "questionGroupId")
    private QuestionGroupEntity questionGroup;

    @Column(name = "genQuestionsCount",nullable = false)
    private int questionsToGenerate;

}
