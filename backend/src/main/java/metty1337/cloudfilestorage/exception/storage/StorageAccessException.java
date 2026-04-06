package metty1337.cloudfilestorage.exception.storage;

public class StorageAccessException extends StorageException {
    public StorageAccessException(Throwable cause) {
        super("Failed to access storage", cause);
    }

    public StorageAccessException() {
        super("Failed to access storage");
    }
}
