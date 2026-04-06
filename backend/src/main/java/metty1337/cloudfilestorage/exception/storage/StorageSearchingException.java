package metty1337.cloudfilestorage.exception.storage;

public class StorageSearchingException extends StorageException {
    public StorageSearchingException(Throwable cause) {
        super("Failed to search.", cause);
    }
}
