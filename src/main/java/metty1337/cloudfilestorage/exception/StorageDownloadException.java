package metty1337.cloudfilestorage.exception;

public class StorageDownloadException extends RuntimeException {
    public StorageDownloadException(Throwable cause) {
        super("Failed to download.", cause);
    }
}
