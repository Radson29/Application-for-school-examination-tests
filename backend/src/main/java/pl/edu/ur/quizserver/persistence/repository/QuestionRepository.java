package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity,Long> {
    QuestionEntity findById(long id);
}
