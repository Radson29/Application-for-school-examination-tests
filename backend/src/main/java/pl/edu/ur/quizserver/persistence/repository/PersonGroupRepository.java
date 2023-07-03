package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.PersonGroupEntity;

import java.util.List;

public interface PersonGroupRepository extends JpaRepository<PersonGroupEntity, Long> {
    PersonGroupEntity findById(long id);
    List<PersonGroupEntity> findDistinctByDeletedAtIsNullOrPeopleIsNullAndDeletedAtIsNull();
    PersonGroupEntity findByName(String name);
}