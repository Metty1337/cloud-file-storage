package metty1337.cloudfilestorage.exception;

public class StorageMoveException extends RuntimeException {
    public StorageMoveException(Throwable cause) {
        super("Failed to move resource.", cause);
    }
}
