package metty1337.cloudfilestorage.exception;

public class StorageObjectDeletionException extends RuntimeException {
    public StorageObjectDeletionException(Throwable cause) {
        super("Failed to delete object.", cause);
    }
}
