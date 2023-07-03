package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.QuestionGroupEntity;

import java.util.List;

public interface QuestionGroupRepository extends JpaRepository<QuestionGroupEntity, Long> {
    List<QuestionGroupEntity> findByCreatorIdAndDeletedAtIsNull(long id);
    QuestionGroupEntity findByName(String name);
    QuestionGroupEntity findById(long id);
}