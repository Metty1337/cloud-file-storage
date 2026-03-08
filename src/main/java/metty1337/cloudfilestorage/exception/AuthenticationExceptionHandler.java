package metty1337.cloudfilestorage.exception;

import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.constants.ExceptionMessages;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        log.warn(ExceptionMessages.BAD_CREDENTIALS_EXCEPTION.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ExceptionMessages.BAD_CREDENTIALS_EXCEPTION.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
