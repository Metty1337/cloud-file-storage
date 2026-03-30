package metty1337.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.StorageMoveRequest;
import metty1337.cloudfilestorage.dto.request.StoragePathRequest;
import metty1337.cloudfilestorage.dto.request.StorageUploadRequest;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.EmptyFileException;
import metty1337.cloudfilestorage.security.UserPrincipal;
import metty1337.cloudfilestorage.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Validated
public class StorageController {

    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<StorageObjectResponse> uploadObject(@RequestParam("object") List<MultipartFile> files, @Valid StorageUploadRequest request, @AuthenticationPrincipal UserPrincipal user) {
        if (files.isEmpty()) {
            throw new EmptyFileException();
        }

        StorageObjectResponse response = storageService.uploadObject(files, request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<StorageObjectResponse> getObjectData(@Valid StoragePathRequest request, @AuthenticationPrincipal UserPrincipal user) {
        StorageObjectResponse response = storageService.getObjectData(request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteObject(@Valid StoragePathRequest request, @AuthenticationPrincipal UserPrincipal user) {
        storageService.deleteObject(request.path(), user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadObject(@Valid StoragePathRequest request, @AuthenticationPrincipal UserPrincipal user) {
        Resource resource = storageService.downloadFile(request.path(), user.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/move")
    public ResponseEntity<StorageObjectResponse> moveObject(@Valid StorageMoveRequest request, @AuthenticationPrincipal UserPrincipal user) {
        StorageObjectResponse response = storageService.moveObject(request.from(), request.to(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/search")
//    public ResponseEntity<StorageSearchRequest> searchObject(@Valid StorageSearchRequest request, @AuthenticationPrincipal UserPrincipal user) {
//
//    }
}
