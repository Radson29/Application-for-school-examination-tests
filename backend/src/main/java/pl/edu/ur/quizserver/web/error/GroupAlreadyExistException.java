package pl.edu.ur.quizserver.web.error;

public class GroupAlreadyExistException extends RuntimeException {
    public GroupAlreadyExistException() {
        super();
    }

    public GroupAlreadyExistException(String message) {
        super(message);
    }

    public GroupAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
