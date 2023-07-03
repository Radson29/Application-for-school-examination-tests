package pl.edu.ur.quizserver.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "question_groups")
public class QuestionGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "creatorId")
    private PersonEntity creator;

    @Column(nullable = false, length = 25)
    private String name;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},mappedBy = "group")
    private List<QuestionEntity> questions;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    public void addQuestion(QuestionEntity question)
    {
        question.setGroup(this);
        this.questions.add(question);
    }
}
