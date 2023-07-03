package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.ScheduleEntity;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity,Long> {
    ScheduleEntity findById(long id);
    List<ScheduleEntity> findByQuizCreatorId(long id);
    List<ScheduleEntity> findByQuizId(long id);
    List<ScheduleEntity> findByAvailableStudentIdAndAvailableInstanceIsNullAndStartsAtBeforeAndEndsAtAfter(long userId, Date dateStarts, Date dateEnds);//todo replace with @Query
}
