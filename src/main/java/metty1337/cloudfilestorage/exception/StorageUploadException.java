package metty1337.cloudfilestorage.exception;

public class StorageUploadException extends RuntimeException {
    public StorageUploadException(Exception e) {
        super("Failed to upload file", e);
    }
}
