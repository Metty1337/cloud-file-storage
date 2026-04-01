package metty1337.cloudfilestorage.dto.response.storage;

public record StorageDirectoryResponse(
        String path,
        String name,
        String type
) implements StorageObjectResponse {
}
