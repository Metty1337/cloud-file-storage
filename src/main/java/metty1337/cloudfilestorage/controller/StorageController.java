package metty1337.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.response.UploadResponse;
import metty1337.cloudfilestorage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam MultipartFile file, @RequestParam String path) {
        UploadResponse response = storageService.uploadFile(file, path);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
}
}
