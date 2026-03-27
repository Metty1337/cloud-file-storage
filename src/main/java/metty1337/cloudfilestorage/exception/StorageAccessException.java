package metty1337.cloudfilestorage.exception;

public class StorageAccessException extends RuntimeException {
    public StorageAccessException(Throwable cause) {
        super("Failed to access storage", cause);
    }
}
