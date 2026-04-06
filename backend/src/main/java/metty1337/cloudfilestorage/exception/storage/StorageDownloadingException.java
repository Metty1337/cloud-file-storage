package metty1337.cloudfilestorage.exception.storage;

public class StorageDownloadingException extends StorageException {
    public StorageDownloadingException(Throwable cause) {
        super("Failed to download.", cause);
    }
}
