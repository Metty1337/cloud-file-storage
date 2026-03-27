package metty1337.cloudfilestorage.dto.response;

public record StorageResponse(
        String path,
        String name,
        long size,
        String type
) {
}
