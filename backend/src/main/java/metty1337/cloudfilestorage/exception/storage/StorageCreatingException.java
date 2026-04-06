package metty1337.cloudfilestorage.exception.storage;

public class StorageCreatingException extends StorageException {
    public StorageCreatingException(Throwable cause) {
        super("Failed to create object.", cause);
    }
}
