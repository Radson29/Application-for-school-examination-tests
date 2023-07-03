package pl.edu.ur.quizserver.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;

public interface LoginRepository extends JpaRepository<LoginEntity,Long> {
    LoginEntity findByLogin(String login);
    LoginEntity findById(long id);
}
