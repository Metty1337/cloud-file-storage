package metty1337.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.constants.ResourceType;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageFileResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.ResourceAlreadyExist;
import metty1337.cloudfilestorage.exception.ResourceNotFoundException;
import metty1337.cloudfilestorage.exception.StorageUploadException;
import metty1337.cloudfilestorage.storage.ObjectData;
import metty1337.cloudfilestorage.storage.StorageClient;
import metty1337.cloudfilestorage.storage.StoragePathResolver;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageClient storageClient;

    public StorageObjectResponse uploadFile(MultipartFile multipartFile, String path, long userId) {
        String resourceName = StoragePathResolver.getResourceName(path, userId) + multipartFile.getOriginalFilename();
        ensureFileNotExist(resourceName);
        upload(multipartFile, resourceName);

        return new StorageFileResponse(
                path,
                multipartFile.getOriginalFilename(),
                multipartFile.getSize(),
                ResourceType.FILE.name()
        );
    }

    public StorageObjectResponse getResourceData(String path, long userId) {
        String resourceName = StoragePathResolver.getResourceName(path, userId);

        ObjectData objectData = storageClient.getStatResponse(resourceName);

        String filePath = StoragePathResolver.getViewFilePath(objectData.name(), userId);
        String name = StoragePathResolver.getFileName(objectData.name());
        long size = objectData.size();

        return new StorageFileResponse(
                filePath,
                name,
                size,
                ResourceType.FILE.name()
        );
    }

    public void deleteFile(String path, long userId) {
        String resourceName = StoragePathResolver.getResourceName(path, userId);
        ensureFileExists(resourceName);
        storageClient.removeFile(resourceName);
    }

    public Resource downloadFile(String path, long userId) {
        String resourceName = StoragePathResolver.getResourceName(path, userId);
        ensureFileExists(resourceName);
        return storageClient.getResource(resourceName);
    }

    public StorageObjectResponse moveResource(String from, String to, long userId) {
        String oldResourceName = StoragePathResolver.getResourceName(from, userId);

        if (StoragePathResolver.isFile(oldResourceName)) {
            ensureFileExists(oldResourceName);

            String newResourceName = StoragePathResolver.getResourceName(to, userId);
            ensureFileNotExist(newResourceName);

            storageClient.moveFile(oldResourceName, newResourceName);

            String newFilePath = StoragePathResolver.getViewFilePath(newResourceName, userId);
            String newFileName = StoragePathResolver.getFileName(newResourceName);
            long size = storageClient.getFileSize(newResourceName);

            return new StorageFileResponse(
                    newFilePath,
                    newFileName,
                    size,
                    ResourceType.FILE.name()
            );
        }
        ensureDirectoryExist(oldResourceName);
        String newResourceName = StoragePathResolver.getResourceName(to, userId);
        storageClient.moveDirectory(oldResourceName, newResourceName);

        return new StorageDirectoryResponse(
                StoragePathResolver.getViewFilePath(newResourceName, userId),
                StoragePathResolver.getDirectoryName(newResourceName),
                ResourceType.DIRECTORY.name()
        );
    }

    private void ensureDirectoryExist(String oldResourceName) {
        if (!storageClient.isDirectoryExist(oldResourceName)) {
            throw new ResourceNotFoundException();
        }
    }

    private void upload(MultipartFile multipartFile, String resourceName) {
        try {
            storageClient.upload(resourceName, multipartFile.getInputStream(), multipartFile.getSize(), multipartFile.getContentType());
        } catch (IOException e) {
            throw new StorageUploadException(e);
        }
    }

    private void ensureFileExists(String resourceName) {
        if (!storageClient.isFileExist(resourceName)) {
            throw new ResourceNotFoundException();
        }
    }

    private void ensureFileNotExist(String resourceName) {
        if (storageClient.isFileExist(resourceName)) {
            throw new ResourceAlreadyExist();
        }
    }
}
