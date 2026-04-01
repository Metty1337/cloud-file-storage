package metty1337.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record StorageDirectoryRequest(
        @NotNull @Pattern(regexp = "(|.+/)", message = "Path must be empty or end with '/'") String path
) {
}
