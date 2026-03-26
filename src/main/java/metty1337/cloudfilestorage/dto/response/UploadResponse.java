package metty1337.cloudfilestorage.dto.response;

public record UploadResponse(
        String path,
        String name,
        long size,
        String type
) {
}
