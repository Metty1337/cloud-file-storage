package metty1337.cloudfilestorage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Directory path request")
public record StorageDirectoryRequest(
        @Schema(description = "Directory path (empty for root, must end with '/' otherwise)", example = "documents/")
        @NotNull @Pattern(regexp = "(|.+/)", message = "Path must be empty or end with '/'") String path
) {
}
