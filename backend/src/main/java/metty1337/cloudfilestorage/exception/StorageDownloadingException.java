package metty1337.cloudfilestorage.exception;

public class StorageDownloadingException extends RuntimeException {
    public StorageDownloadingException(Throwable cause) {
        super("Failed to download.", cause);
    }
}
