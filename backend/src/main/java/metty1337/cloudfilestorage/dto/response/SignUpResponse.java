package metty1337.cloudfilestorage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sign-up response")
public record SignUpResponse(
        @Schema(description = "Registered username", example = "johndoe")
        String username
) {
}
