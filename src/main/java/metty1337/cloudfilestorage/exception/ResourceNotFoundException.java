package metty1337.cloudfilestorage.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "Resource not found";

    public ResourceNotFoundException(Throwable cause) {
        super(EXCEPTION_MESSAGE, cause);
    }

    public ResourceNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }
}
