package pl.edu.ur.quizserver.persistence.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.ur.quizserver.web.util.Views;

import javax.persistence.*;
import java.util.List;

@JsonView(Views.QuizStudent.class)
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "quizzes")
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.QuizStudent.class)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "creatorId")
    private PersonEntity creator;

    @JsonView(Views.QuizTeacherPublic.class)
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE} , mappedBy = "quiz")
    private List<QGRecipeEntity> questionRecipes;

    @Column(nullable = false, length = 25)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private short quizTime;

    public void setQuestionModules(List<QGRecipeEntity> questionModules) {
        for(QGRecipeEntity qgModule : questionModules)
            qgModule.setQuiz(this);
        this.questionRecipes = questionModules;
    }
}
