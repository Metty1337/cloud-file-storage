package metty1337.cloudfilestorage.dto.request.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Storage object path request")
public record StoragePathRequest(
        @Schema(description = "Path to a file or directory (directories end with '/')", example = "documents/report.pdf")
        @NotBlank @Pattern(regexp = "^(?!\\.\\.)(?!.*/\\.\\.)(?!.*\\.\\.$)[^/].*") String path
) {
}
