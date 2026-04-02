package metty1337.cloudfilestorage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sign-in response")
public record SignInResponse(
        @Schema(description = "Authenticated username", example = "johndoe")
        String username
) {
}
