package antifraud.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionsHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = JdbcSQLIntegrityConstraintViolationException.class)
    public void handleJdbcSQLIntegrityConstraintViolationException(
            JdbcSQLIntegrityConstraintViolationException e, WebRequest request) {
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(NoChangeException.class)
    public void handleNoChangeException(NoChangeException e, WebRequest request) {
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity handleUserNotFoundException(NotFoundException e, WebRequest request) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            PropertyValueException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity handlePropertyValueException(Exception e, WebRequest request) {
        return ResponseEntity.badRequest().build();
    }
}
