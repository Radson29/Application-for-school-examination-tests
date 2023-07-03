package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.GeneratedQuestionEntity;

public interface GeneratedQuestionRepository extends JpaRepository<GeneratedQuestionEntity, Long> {
}

