package metty1337.cloudfilestorage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Search request")
public record StorageSearchRequest(
        @Schema(description = "Search query string", example = "report")
        @NotBlank String query
) {
}
