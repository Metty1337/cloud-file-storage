package metty1337.cloudfilestorage.exception.storage;

public class StorageUploadException extends StorageException {
    public StorageUploadException(Throwable cause) {
        super("Failed to upload file", cause);
    }
}
