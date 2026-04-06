package metty1337.cloudfilestorage.dto.request.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Search request")
public record StorageSearchRequest(
        @Schema(description = "Search query string", example = "report")
        @NotBlank @Size(max = 255) String query
) {
}
