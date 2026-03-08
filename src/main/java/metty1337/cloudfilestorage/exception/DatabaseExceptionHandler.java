package metty1337.cloudfilestorage.exception;

import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.constants.ExceptionMessages;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DatabaseExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn(ExceptionMessages.USER_DATA_INTEGRITY_VIOLATION_EXCEPTION.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ExceptionMessages.USER_DATA_INTEGRITY_VIOLATION_EXCEPTION.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
