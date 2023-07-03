package pl.edu.ur.quizserver.web.error;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException() {
        super();
    }

    public ScheduleNotFoundException(String message) {
        super(message);
    }

    public ScheduleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}