package pl.edu.ur.quizserver.web.error;

public class UserNotExistInGroupException extends RuntimeException {
    public UserNotExistInGroupException() {
        super();
    }

    public UserNotExistInGroupException(String message) {
        super(message);
    }

    public UserNotExistInGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotExistInGroupException(Throwable cause) {
        super(cause);
    }
}
