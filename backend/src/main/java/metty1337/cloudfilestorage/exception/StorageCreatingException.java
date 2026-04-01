package metty1337.cloudfilestorage.exception;

public class StorageCreatingException extends RuntimeException {
    public StorageCreatingException(Throwable cause) {
        super("Failed to create object.", cause);
    }
}
