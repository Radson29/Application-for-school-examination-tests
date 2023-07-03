package pl.edu.ur.quizserver.web.error;

public class QGInvalidQuestionCountException extends RuntimeException {
    public QGInvalidQuestionCountException() {
    }

    public QGInvalidQuestionCountException(String message) {
        super(message);
    }

    public QGInvalidQuestionCountException(String message, Throwable cause) {
        super(message, cause);
    }

    public QGInvalidQuestionCountException(Throwable cause) {
        super(cause);
    }
}
