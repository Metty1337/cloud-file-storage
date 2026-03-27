package metty1337.cloudfilestorage.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(Throwable cause) {
        super("Resource not found", cause);
    }
}
