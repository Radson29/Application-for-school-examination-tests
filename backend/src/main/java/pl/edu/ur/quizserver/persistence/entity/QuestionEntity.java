    package pl.edu.ur.quizserver.persistence.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Table(name = "questions")
    public class QuestionEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @JsonIgnore
        @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
        @JoinColumn(name = "groupId")
        private QuestionGroupEntity group;

        @Column(nullable = false)
        private String value;

        @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},mappedBy = "question")
        private List<AnswerEntity> answers;

        public void setAnswers(List<AnswerEntity> answers) {
            for(AnswerEntity answer : answers)
                answer.setQuestion(this);
            this.answers = answers;
        }
    }
