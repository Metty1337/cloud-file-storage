package metty1337.cloudfilestorage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import metty1337.cloudfilestorage.dto.request.validation.ValidConsistentObjectType;

@ValidConsistentObjectType
@Schema(description = "Move/rename request. Both paths must be of the same type (both files or both directories)")
public record StorageMoveRequest(
        @Schema(description = "Source path", example = "documents/report.pdf")
        @NotBlank String from,

        @Schema(description = "Destination path", example = "archive/report.pdf")
        @NotBlank String to
) {
}


