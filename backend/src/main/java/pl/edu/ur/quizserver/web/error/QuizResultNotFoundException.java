package pl.edu.ur.quizserver.web.error;

public class QuizResultNotFoundException extends RuntimeException {
    public QuizResultNotFoundException() {
        super();
    }

    public QuizResultNotFoundException(String message) {
        super(message);
    }

    public QuizResultNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
