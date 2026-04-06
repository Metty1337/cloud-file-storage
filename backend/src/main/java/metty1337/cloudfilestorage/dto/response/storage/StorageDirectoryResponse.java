package metty1337.cloudfilestorage.dto.response.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import metty1337.cloudfilestorage.constants.ObjectType;

@Schema(description = "Directory metadata")
public record StorageDirectoryResponse(
        @Schema(description = "Full path to the directory", example = "documents/")
        String path,

        @Schema(description = "Directory name", example = "documents")
        String name,

        @Schema(description = "Object type", example = "DIRECTORY")
        ObjectType type
) implements StorageObjectResponse {
}
