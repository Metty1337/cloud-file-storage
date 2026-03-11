package metty1337.cloudfilestorage.exception;

import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.constants.ExceptionMessages;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ExceptionMessages.METHOD_ARGUMENT_NOT_VALID_EXCEPTION.getMessage());

        String errorMessage = getErrorMessage(ex);
        return new ResponseEntity<>(new ErrorResponse(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException e) {
        log.warn(ExceptionMessages.USER_ALREADY_EXIST_EXCEPTION.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ExceptionMessages.USER_ALREADY_EXIST_EXCEPTION.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        log.warn(ExceptionMessages.BAD_CREDENTIALS_EXCEPTION.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ExceptionMessages.BAD_CREDENTIALS_EXCEPTION.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(ExceptionMessages.INTERNAL_SERVER_ERROR.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static @NonNull String getErrorMessage(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
