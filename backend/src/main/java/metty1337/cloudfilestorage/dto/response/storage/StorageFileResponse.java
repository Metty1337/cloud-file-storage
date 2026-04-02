package metty1337.cloudfilestorage.dto.response.storage;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "File metadata")
public record StorageFileResponse(
        @Schema(description = "Full path to the file", example = "documents/report.pdf")
        String path,

        @Schema(description = "File name", example = "report.pdf")
        String name,

        @Schema(description = "File size in bytes", example = "102400")
        long size,

        @Schema(description = "Object type", example = "FILE")
        String type
) implements StorageObjectResponse {
}
