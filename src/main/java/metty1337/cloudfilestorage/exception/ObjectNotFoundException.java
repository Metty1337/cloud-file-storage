package metty1337.cloudfilestorage.exception;

public class ObjectNotFoundException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "Resource not found";

    public ObjectNotFoundException(Throwable cause) {
        super(EXCEPTION_MESSAGE, cause);
    }

    public ObjectNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }
}
