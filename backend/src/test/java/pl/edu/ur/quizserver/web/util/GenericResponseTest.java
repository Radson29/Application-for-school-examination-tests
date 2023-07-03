package pl.edu.ur.quizserver.web.util;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenericResponseTest {

    @Test
    void testGetMessage() {
        String testMessage = "testowa wiadomość";
        GenericResponse genericResponse = new GenericResponse("");

        genericResponse.setMessage(testMessage);

        assertEquals(testMessage, genericResponse.getMessage());
    }

    @Test
    void testGetError() {
        String testError = "testowy error";
        GenericResponse genericResponse = new GenericResponse("");

        genericResponse.setError(testError);

        assertEquals(testError, genericResponse.getError());
    }

    @Test
    void testGenericResponseWithErrors() {
        List<ObjectError> allErrorsList = new ArrayList<>();
        allErrorsList.add(new FieldError("objectName", "fieldName", "errorMessage"));

        GenericResponse response = new GenericResponse(allErrorsList, "error");

        assertEquals("[{\"field\":\"fieldName\",\"defaultMessage\":\"errorMessage\"}]", response.getMessage());
        assertEquals("error", response.getError());
    }
}