package metty1337.cloudfilestorage.exception;

public class StorageDeleteException extends RuntimeException {
    public StorageDeleteException(Throwable cause) {
        super("Failed to delete.", cause);
    }
}
