package pl.edu.ur.quizserver.web.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;

import java.util.List;

public interface LoginService extends UserDetailsService {
    PasswordEncoder passwordEncoder();
    LoginEntity findByLogin(String login);
    LoginEntity findById(long id);
    List<LoginEntity> findAll();
    void save(LoginEntity login, boolean encodePassword);
    String generateNewPassword(LoginEntity login);
    String resetUser(long id, String type);
    boolean isValidUser(LoginEntity login, String personType);
}
