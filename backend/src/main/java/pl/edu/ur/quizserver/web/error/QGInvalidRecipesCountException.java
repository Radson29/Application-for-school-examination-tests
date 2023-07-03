package pl.edu.ur.quizserver.web.error;

public class QGInvalidRecipesCountException extends RuntimeException {
    public QGInvalidRecipesCountException() {
        super();
    }

    public QGInvalidRecipesCountException(String message) {
        super(message);
    }

    public QGInvalidRecipesCountException(String message, Throwable cause) {
        super(message, cause);
    }

    public QGInvalidRecipesCountException(Throwable cause) {
        super(cause);
    }
}
