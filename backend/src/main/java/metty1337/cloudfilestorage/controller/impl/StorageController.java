package metty1337.cloudfilestorage.controller.impl;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.controller.StorageControllerApi;
import metty1337.cloudfilestorage.dto.request.StorageMoveRequest;
import metty1337.cloudfilestorage.dto.request.StoragePathRequest;
import metty1337.cloudfilestorage.dto.request.StorageSearchRequest;
import metty1337.cloudfilestorage.dto.request.StorageUploadRequest;
import metty1337.cloudfilestorage.dto.response.DownloadResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.EmptyFileException;
import metty1337.cloudfilestorage.security.UserPrincipal;
import metty1337.cloudfilestorage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Validated
public class StorageController implements StorageControllerApi {

    private final StorageService storageService;

    @PostMapping
    @Override
    public ResponseEntity<StorageObjectResponse> uploadObject(@RequestParam("object") List<MultipartFile> files, @Valid StorageUploadRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        if (files.isEmpty()) {
            throw new EmptyFileException();
        }

        StorageObjectResponse response = storageService.uploadObject(files, request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Override
    public ResponseEntity<StorageObjectResponse> getObjectData(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        StorageObjectResponse response = storageService.getObjectData(request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Void> deleteObject(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        storageService.deleteObject(request.path(), user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/download")
    @Override
    public ResponseEntity<StreamingResponseBody> downloadObject(@Valid StoragePathRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        DownloadResponse result = storageService.downloadObject(request.path(), user.getId());
        StreamingResponseBody stream = result.body()::writeTo;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(stream);
    }

    @PutMapping("/move")
    @Override
    public ResponseEntity<StorageObjectResponse> moveObject(@Valid StorageMoveRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        StorageObjectResponse response = storageService.moveObject(request.from(), request.to(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Override
    public ResponseEntity<List<StorageObjectResponse>> searchObject(@Valid StorageSearchRequest request, @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        List<StorageObjectResponse> response = storageService.searchObject(request.query(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
