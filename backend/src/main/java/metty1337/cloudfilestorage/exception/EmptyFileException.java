package metty1337.cloudfilestorage.exception;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super("Empty file");
    }
}
