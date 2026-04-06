package metty1337.cloudfilestorage.dto.request.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "File upload request")
public record StorageUploadRequest(
        @Schema(description = "Upload directory path (empty for root, must end with '/' otherwise)", example = "documents/")
        @NotNull @Pattern(regexp = "^(?!\\.\\.)(?!.*/\\.\\.)(?!.*\\.\\.$)(|[^/].*/)$", message = "Path must be empty or end with '/'") String path) {
}
