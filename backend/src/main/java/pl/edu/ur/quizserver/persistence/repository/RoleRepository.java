package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.PermissionEntity;
import pl.edu.ur.quizserver.persistence.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity,Long> {
    RoleEntity findByName(String name);
}
