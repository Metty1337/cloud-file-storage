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
import metty1337.cloudfilestorage.dto.request.StorageMoveRequest;
import metty1337.cloudfilestorage.dto.request.StoragePathRequest;
import metty1337.cloudfilestorage.dto.request.StorageSearchRequest;
import metty1337.cloudfilestorage.dto.request.StorageUploadRequest;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.EmptyFileException;
import metty1337.cloudfilestorage.security.UserPrincipal;
import metty1337.cloudfilestorage.service.StorageService;
import metty1337.cloudfilestorage.storage.StoragePathResolver;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Validated
@Tag(name = "Storage", description = "Storage object operations (files and folders)")
public class StorageController {

    private final StorageService storageService;

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
    public ResponseEntity<StorageObjectResponse> uploadObject(@RequestParam("object") List<MultipartFile> files, @Valid StorageUploadRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        if (files.isEmpty()) {
            throw new EmptyFileException();
        }

        StorageObjectResponse response = storageService.uploadObject(files, request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

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
    public ResponseEntity<StorageObjectResponse> getObjectData(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        StorageObjectResponse response = storageService.getObjectData(request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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
    public ResponseEntity<Void> deleteObject(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        storageService.deleteObject(request.path(), user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

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
    public ResponseEntity<StreamingResponseBody> downloadObject(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        if (StoragePathResolver.isFile(request.path())) {
            Resource resource = storageService.downloadFile(request.path(), user.getId());
            StreamingResponseBody stream = output -> {
                try (InputStream inputStream = resource.getInputStream()) {
                    inputStream.transferTo(output);
                }
            };
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(stream);
        }

        StreamingResponseBody stream = output -> storageService.downloadFolder(request.path(), user.getId(), output);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(stream);
    }

    @GetMapping("/move")
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
    public ResponseEntity<StorageObjectResponse> moveObject(@Valid StorageMoveRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        StorageObjectResponse response = storageService.moveObject(request.from(), request.to(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Search objects", description = "Searches for objects matching the query")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results"),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<StorageObjectResponse>> searchObject(@Valid StorageSearchRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        List<StorageObjectResponse> response = storageService.searchObject(request.query(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
