package metty1337.cloudfilestorage.dto.response.storage;

public record StorageFileResponse(
        String path,
        String name,
        long size,
        String type
) implements StorageObjectResponse {
}
