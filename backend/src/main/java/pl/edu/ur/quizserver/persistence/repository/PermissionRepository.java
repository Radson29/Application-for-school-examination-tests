package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.PermissionEntity;

public interface PermissionRepository extends JpaRepository<PermissionEntity,Long> {
}
