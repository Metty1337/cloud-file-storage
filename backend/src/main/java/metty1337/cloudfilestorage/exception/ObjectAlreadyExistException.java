package metty1337.cloudfilestorage.exception;

public class ObjectAlreadyExistException extends RuntimeException {
    public ObjectAlreadyExistException() {
        super("Resource already exist");
    }
}
