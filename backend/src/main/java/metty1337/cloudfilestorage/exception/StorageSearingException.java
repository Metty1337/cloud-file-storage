package metty1337.cloudfilestorage.exception;

public class StorageSearingException extends RuntimeException {
    public StorageSearingException(Throwable cause) {
        super("Failed to search.", cause);
    }
}
