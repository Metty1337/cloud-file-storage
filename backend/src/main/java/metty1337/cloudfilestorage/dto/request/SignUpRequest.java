package metty1337.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotNull @NotBlank @Size(min = 5, max = 20) String username,
        @NotNull @NotBlank @Size(min = 5, max = 20) String password
) {
}
