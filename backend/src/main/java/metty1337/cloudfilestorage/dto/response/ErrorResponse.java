package metty1337.cloudfilestorage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response")
public record ErrorResponse(
        @Schema(description = "Error message", example = "Object not found")
        String message
) {
}
