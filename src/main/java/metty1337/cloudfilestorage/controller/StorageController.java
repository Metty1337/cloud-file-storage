package metty1337.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.StoragePathRequest;
import metty1337.cloudfilestorage.dto.response.StorageResponse;
import metty1337.cloudfilestorage.exception.EmptyFileException;
import metty1337.cloudfilestorage.security.UserPrincipal;
import metty1337.cloudfilestorage.service.StorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Validated
public class StorageController {

    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<StorageResponse> uploadFile(@RequestParam MultipartFile file, @Valid StoragePathRequest request, @AuthenticationPrincipal UserPrincipal user) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        StorageResponse response = storageService.uploadFile(file, request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<StorageResponse> getResourceData(@Valid StoragePathRequest request, @AuthenticationPrincipal UserPrincipal user) {
        StorageResponse response = storageService.getResourceData(request.path(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(@Valid StoragePathRequest request, @AuthenticationPrincipal UserPrincipal user) {
        storageService.deleteResource(request.path(), user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@Valid StoragePathRequest request, @AuthenticationPrincipal UserPrincipal user) {
        Resource resource = storageService.downloadFile(request.path(), user.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
