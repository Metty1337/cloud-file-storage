package metty1337.cloudfilestorage.exception;

public class ResourceAlreadyExist extends RuntimeException {
    public ResourceAlreadyExist() {
        super("Resource already exist");
    }
}
