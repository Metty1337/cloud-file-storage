package metty1337.cloudfilestorage.dto.response.storage;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Directory metadata")
public record StorageDirectoryResponse(
        @Schema(description = "Full path to the directory", example = "documents/")
        String path,

        @Schema(description = "Directory name", example = "documents")
        String name,

        @Schema(description = "Object type", example = "DIRECTORY")
        String type
) implements StorageObjectResponse {
}
