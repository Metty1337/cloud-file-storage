package metty1337.cloudfilestorage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User profile response")
public record UserResponse(
        @Schema(description = "Username", example = "johndoe")
        String username
) {
}
