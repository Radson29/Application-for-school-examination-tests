package pl.edu.ur.quizserver.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "quiz_results")
public class QuizResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "personId")
    private PersonEntity person;

    private int score;

    private int questionsCount;

    @Column(nullable = false)
    private String quizTitle;

    @Column(nullable = false)
    private String quizCreator;

    @JsonIgnore
    @Column(nullable = false, columnDefinition="TEXT")
    private String quizHistory;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;
}
