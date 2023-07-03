package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.AnswerEntity;

import java.util.List;

public interface AnswerRepository extends JpaRepository<AnswerEntity,Long> {
    List<AnswerEntity> findByQuestionIdAndCorrectIsTrue(long questionId);
}
