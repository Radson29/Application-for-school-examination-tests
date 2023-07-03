package pl.edu.ur.quizserver.persistence.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.ur.quizserver.web.util.Views;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonView(Views.QuizStudent.class)
@Entity
@Getter
@Setter
@NoArgsConstructor
@Data
@Table(name  = "schedules")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "quizId")
    private QuizEntity quiz;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startsAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endsAt;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true,
            mappedBy = "schedule")
    private List<AvailableQuizEntity> available;

    public List<PersonEntity> getAvailableFor()
    {
        return available.stream().map(a -> a.getStudent()).collect(Collectors.toList());
    }
    public void setAvailable(List<AvailableQuizEntity> available) {
        for(AvailableQuizEntity quiz : available)
            quiz.setSchedule(this);
        this.available = available;
    }
}
