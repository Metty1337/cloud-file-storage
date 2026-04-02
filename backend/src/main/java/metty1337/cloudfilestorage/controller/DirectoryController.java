package metty1337.cloudfilestorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.StorageDirectoryRequest;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.security.UserPrincipal;
import metty1337.cloudfilestorage.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
@Validated
@Tag(name = "Directory", description = "Directory listing and creation")
public class DirectoryController {

    private final StorageService storageService;

    @GetMapping
    @Operation(summary = "List directory contents", description = "Returns all objects in the specified directory")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Directory contents retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Directory not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<StorageObjectResponse>> getDirectory(@Valid StorageDirectoryRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        List<StorageObjectResponse> response = storageService.getDirectoryContents(request.path(), user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create directory", description = "Creates a new directory at the specified path")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Directory created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Directory already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<StorageDirectoryResponse> createDirectory(@Valid StorageDirectoryRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        StorageDirectoryResponse response = storageService.createDirectory(request.path(), user.getId());
        return ResponseEntity.ok(response);
    }
}
