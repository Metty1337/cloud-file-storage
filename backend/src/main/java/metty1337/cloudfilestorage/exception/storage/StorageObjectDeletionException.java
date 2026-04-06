package metty1337.cloudfilestorage.exception.storage;

public class StorageObjectDeletionException extends StorageException {
    public StorageObjectDeletionException(Throwable cause) {
        super("Failed to delete object.", cause);
    }
}
