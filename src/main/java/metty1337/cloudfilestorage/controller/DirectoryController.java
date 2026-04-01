package metty1337.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.StorageDirectoryRequest;
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
public class DirectoryController {

    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<List<StorageObjectResponse>> getDirectory(@Valid StorageDirectoryRequest request, @AuthenticationPrincipal UserPrincipal user) {
        List<StorageObjectResponse> response = storageService.getDirectoryContents(request.path(), user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StorageDirectoryResponse> createDirectory(@Valid StorageDirectoryRequest request, @AuthenticationPrincipal UserPrincipal user) {
        StorageDirectoryResponse response = storageService.createDirectory(request.path(), user.getId());
        return ResponseEntity.ok(response);
    }
}
