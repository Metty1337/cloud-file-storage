package metty1337.cloudfilestorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import metty1337.cloudfilestorage.dto.response.UserResponse;
import metty1337.cloudfilestorage.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User profile operations")
public class UserController {

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the profile of the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> me(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = new UserResponse(userPrincipal.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
