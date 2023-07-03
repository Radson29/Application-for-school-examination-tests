package pl.edu.ur.quizserver.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.ur.quizserver.web.util.Views;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@JsonView(Views.QuizStudent.class)
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "quiz_instances")
public class QuizInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "quizId")
    private QuizEntity quiz;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "studentId")
    private PersonEntity student;

    @OneToMany(mappedBy = "quizInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratedQuestionEntity> generatedQuestions;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startedAt;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            mappedBy = "instance")
    private AvailableQuizEntity available;
}
