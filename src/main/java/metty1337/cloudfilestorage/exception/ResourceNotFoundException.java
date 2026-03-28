package metty1337.cloudfilestorage.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Throwable cause) {
        super("Resource not found", cause);
    }
}
