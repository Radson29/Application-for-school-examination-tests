package pl.edu.ur.quizserver.web.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.edu.ur.quizserver.web.util.GenericResponse;

@ControllerAdvice
public class ErrorExceptionHandler extends ResponseEntityExceptionHandler {

    public ErrorExceptionHandler() {
        super();
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final BindingResult result = ex.getBindingResult();
        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final BindingResult result = ex.getBindingResult();
        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ UserNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse("Error 400", "User not found");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ UserAlreadyExistException.class })
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse("Error 400", "User already exist");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ GroupAlreadyExistException.class })
    public ResponseEntity<Object> handleGroupAlreadyExist(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse("Error 400", "Group already exist");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ UserAlreadyExistInGroupException.class })
    public ResponseEntity<Object> handleUserExistInGroup(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse("Error 400", "User already exist in group");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ GroupNotFoundException.class })
    public ResponseEntity<Object> handleGroupNotFound(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse("Error 400", "Group not found");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ QGInvalidQuestionCountException.class })
    public ResponseEntity<Object> handleInvalidQuetionCount(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse("Error 400", "Invalid Question Count");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ QGInvalidRecipesCountException.class })
    public ResponseEntity<Object> handleInvalidMo(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse("Error 400", "Invalid Modules Count");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
