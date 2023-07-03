package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;

import java.util.List;

public interface PeopleRepository extends JpaRepository<PersonEntity, Long> {
    PersonEntity findById(long id);
    PersonEntity findByPesel(String pesel);
    List<PersonEntity> findByLoginRoleNameAndDeletedAtIsNullOrderByFirstName(String type);
    List<PersonEntity> findByDeletedAtIsNullAndGroups_id(long id);
}
