package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.QuizResultEntity;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResultEntity,Long> {
    QuizResultEntity findById(long id);
    List<QuizResultEntity> findByPersonId(long id);
    List<QuizResultEntity> findByQuizCreator(String creator);
}
