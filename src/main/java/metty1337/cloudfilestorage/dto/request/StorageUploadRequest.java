package metty1337.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StorageUploadRequest(
        @NotBlank @Pattern(regexp = ".+/$", message = "Path must end with '/'") String path
) {
}
