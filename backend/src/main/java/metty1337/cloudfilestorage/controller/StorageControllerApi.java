package metty1337.cloudfilestorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import metty1337.cloudfilestorage.dto.request.storage.StorageMoveRequest;
import metty1337.cloudfilestorage.dto.request.storage.StoragePathRequest;
import metty1337.cloudfilestorage.dto.request.storage.StorageSearchRequest;
import metty1337.cloudfilestorage.dto.request.storage.StorageUploadRequest;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

@Tag(name = "Storage", description = "Storage object operations (files and folders)")
public interface StorageControllerApi {
    @PostMapping
    @Operation(summary = "Upload objects", description = "Uploads one or more objects to the specified path")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Objects uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Empty object or invalid path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Object already exists at the specified path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<StorageObjectResponse> uploadObject(@NotEmpty @RequestParam("object") List<MultipartFile> files, @Valid StorageUploadRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) throws IOException;

    @GetMapping
    @Operation(summary = "Get object metadata", description = "Returns metadata for an object at the specified path")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Object metadata retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<StorageObjectResponse> getObjectData(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user);

    @DeleteMapping
    @Operation(summary = "Delete object", description = "Deletes an object at the specified path")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Object deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteObject(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user);

    @GetMapping("/download")
    @Operation(summary = "Download object", description = "Downloads an object as a raw stream or a folder as a ZIP archive")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Object stream or ZIP archive",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<StreamingResponseBody> downloadObject(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user);

    @PutMapping("/move")
    @Operation(summary = "Move or rename object", description = "Moves an object from one path to another")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Object moved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid move request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Source object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Object already exists at target path",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<StorageObjectResponse> moveObject(@Valid StorageMoveRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user);

    @GetMapping("/search")
    @Operation(summary = "Search objects", description = "Searches for objects matching the query")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results"),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<StorageObjectResponse>> searchObject(@Valid StorageSearchRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user);
}
