package pl.edu.ur.quizserver.web.error;

public class UserAlreadyExistInGroupException extends RuntimeException {

    public UserAlreadyExistInGroupException() {
        super();
    }

    public UserAlreadyExistInGroupException(String message) {
        super(message);
    }

    public UserAlreadyExistInGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistInGroupException(Throwable cause) {
        super(cause);
    }
}