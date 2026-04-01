package metty1337.cloudfilestorage.exception;

public class StorageMovementException extends RuntimeException {
    public StorageMovementException(Throwable cause) {
        super("Failed to move object.", cause);
    }
}
