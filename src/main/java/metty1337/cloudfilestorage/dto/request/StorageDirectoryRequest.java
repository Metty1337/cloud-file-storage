package metty1337.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StorageDirectoryRequest(
        @NotBlank @Pattern(regexp = ".+/$", message = "Path must end with '/'") String path
) {
}
