package pl.edu.ur.quizserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication()
public class QuizServerApplication {

    @Value("${db.ip}")
    private String dbIp;

    @Value("${db.port}")
    private int dbPort;

    @Value("${db.name}")
    private String dbName;

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    public static void main(String[] args) {
        SpringApplication.run(QuizServerApplication.class, args);
    }

}
