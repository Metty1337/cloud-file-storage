package metty1337.cloudfilestorage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Sign-up request")
public record SignUpRequest(
        @Schema(description = "Username", example = "johndoe", minLength = 5, maxLength = 20)
        @NotNull @NotBlank @Size(min = 5, max = 20) String username,

        @Schema(description = "Password", example = "secret", minLength = 5, maxLength = 20)
        @NotNull @NotBlank @Size(min = 5, max = 20) String password
) {
}
