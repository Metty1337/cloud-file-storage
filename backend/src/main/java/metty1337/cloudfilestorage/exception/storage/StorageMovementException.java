package metty1337.cloudfilestorage.exception.storage;

public class StorageMovementException extends StorageException {
    public StorageMovementException(Throwable cause) {
        super("Failed to move object.", cause);
    }
}
