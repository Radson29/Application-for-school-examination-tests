package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.QuizInstanceEntity;

import java.util.List;

public interface QuizInstanceRepository extends JpaRepository<QuizInstanceEntity,Long> {
    QuizInstanceEntity findById(long id);
    List<QuizInstanceEntity> findByQuizId(long id);
}
