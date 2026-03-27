package metty1337.cloudfilestorage.exception;

public class StorageUploadException extends RuntimeException {
    public StorageUploadException(Throwable cause) {
        super("Failed to upload file", cause);
    }
}
