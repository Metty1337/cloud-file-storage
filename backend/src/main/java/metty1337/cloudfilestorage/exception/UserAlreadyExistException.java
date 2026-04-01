package metty1337.cloudfilestorage.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(Throwable cause) {
        super("Username is already in use", cause);
    }
}
