package antifraud.api;

import antifraud.exceptions.NoChangeException;
import antifraud.exceptions.UserNotFoundException;
import antifraud.models.AppUser;
import antifraud.models.Registration;
import antifraud.models.UserAccess;
import antifraud.models.UserRole;
import antifraud.services.UserService;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<AppUser> register(@RequestBody @Valid Registration request) {
        AppUser user = userService.registerUser(request);
        return new ResponseEntity(user, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<Iterable<AppUser>> listUsers() {
        Iterable<AppUser> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("username", username);
        response.put("status", "Deleted successfully!");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/role")
    public ResponseEntity<AppUser> updateRole(@RequestBody UserRole userRole) {
        AppUser user = userService.updateRole(userRole);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/access")
    public ResponseEntity<Map<String, String>> updateAccess(@RequestBody UserAccess userAccess) {
        userService.updateAccess(userAccess);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "User %s %s!".formatted(userAccess.username(), userAccess.operation() + "ed"));
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(value = JdbcSQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleJdbcSQLIntegrityConstraintViolationException(
            JdbcSQLIntegrityConstraintViolationException e, WebRequest request) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Please use another username");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {
            PropertyValueException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity handlePropertyValueException(Exception e, WebRequest request) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(NoChangeException.class)
    public void handleNoChangeException(NoChangeException e, WebRequest request) {
    }
}
