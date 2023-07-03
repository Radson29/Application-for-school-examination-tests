package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.AvailableQuizEntity;

public interface AvailableQuizRepository extends JpaRepository<AvailableQuizEntity,Long> {
    AvailableQuizEntity findByStudentIdAndScheduleId(long studentId, long scheduleId);
}

