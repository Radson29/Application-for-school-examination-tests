package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.QuizEntity;

import java.util.List;

public interface QuizRepository extends JpaRepository<QuizEntity,Long> {
    QuizEntity findById(long id);
    List<QuizEntity> findByCreatorId(long id);
}
