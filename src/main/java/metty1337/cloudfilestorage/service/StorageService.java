package metty1337.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.constants.ObjectType;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageFileResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.ObjectAlreadyExistException;
import metty1337.cloudfilestorage.exception.ObjectNotFoundException;
import metty1337.cloudfilestorage.exception.StorageUploadException;
import metty1337.cloudfilestorage.storage.ObjectData;
import metty1337.cloudfilestorage.storage.StorageClient;
import metty1337.cloudfilestorage.storage.StoragePathResolver;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageClient storageClient;

    public StorageObjectResponse uploadObject(List<MultipartFile> files, String path, long userId) {
        for (MultipartFile file : files) {
            String objectName = StoragePathResolver.getObjectName(path, userId) + file.getOriginalFilename();

            ensureFileNotExist(objectName);
            uploadFile(file, objectName);
        }

        MultipartFile object = files.getFirst();
        if (isDirectoryFiles(files)) {
            return new StorageDirectoryResponse(
                    path,
                    StoragePathResolver.getParentDirectory(Objects.requireNonNull(object.getOriginalFilename())),
                    ObjectType.DIRECTORY.name()
            );
        } else {
            return new StorageFileResponse(
                    path,
                    object.getOriginalFilename(),
                    object.getSize(),
                    ObjectType.FILE.name()
            );
        }
    }

    private static boolean isDirectoryFiles(List<MultipartFile> files) {
        return files.size() > 1;
    }

    public StorageObjectResponse getObjectData(String path, long userId) {
        String objectName = StoragePathResolver.getObjectName(path, userId);

        ObjectData objectData = storageClient.getObjectData(objectName);

        String filePath = StoragePathResolver.getViewFilePath(objectData.name(), userId);
        String name = StoragePathResolver.getFileName(objectData.name());
        long size = objectData.size();

        return new StorageFileResponse(
                filePath,
                name,
                size,
                ObjectType.FILE.name()
        );
    }

    public void deleteObject(String path, long userId) {
        String objectName = StoragePathResolver.getObjectName(path, userId);
        if (StoragePathResolver.isFile(objectName)) {
            deleteFile(objectName);
        } else {
            deleteDirectory(objectName);
        }
    }

    public Resource downloadFile(String path, long userId) {
        String objectName = StoragePathResolver.getObjectName(path, userId);
        ensureFileExists(objectName);
        return storageClient.getObject(objectName);
    }

    public StorageObjectResponse moveObject(String from, String to, long userId) {
        String oldObjectName = StoragePathResolver.getObjectName(from, userId);

        if (StoragePathResolver.isFile(oldObjectName)) {
            ensureFileExists(oldObjectName);

            String newObjectName = StoragePathResolver.getObjectName(to, userId);
            ensureFileNotExist(newObjectName);

            storageClient.moveFile(oldObjectName, newObjectName);

            String newFilePath = StoragePathResolver.getViewFilePath(newObjectName, userId);
            String newFileName = StoragePathResolver.getFileName(newObjectName);
            long size = storageClient.getFileSize(newObjectName);

            return new StorageFileResponse(
                    newFilePath,
                    newFileName,
                    size,
                    ObjectType.FILE.name()
            );
        }
        ensureDirectoryExist(oldObjectName);
        String newObjectName = StoragePathResolver.getObjectName(to, userId);
        storageClient.moveDirectory(oldObjectName, newObjectName);

        return new StorageDirectoryResponse(
                StoragePathResolver.getViewFilePath(newObjectName, userId),
                StoragePathResolver.getDirectoryName(newObjectName),
                ObjectType.DIRECTORY.name()
        );
    }

    private void deleteDirectory(String objectName) {
        ensureDirectoryExist(objectName);
        storageClient.removeDirectory(objectName);
    }

    private void deleteFile(String objectName) {
        ensureFileExists(objectName);
        storageClient.removeFile(objectName);
    }

    private void ensureDirectoryExist(String objectName) {
        if (!storageClient.isDirectoryExist(objectName)) {
            throw new ObjectNotFoundException();
        }
    }

    private void uploadFile(MultipartFile multipartFile, String objectName) {
        try {
            storageClient.uploadFile(objectName, multipartFile.getInputStream(), multipartFile.getSize(), multipartFile.getContentType());
        } catch (IOException e) {
            throw new StorageUploadException(e);
        }
    }

    private void ensureFileExists(String objectName) {
        if (!storageClient.isFileExist(objectName)) {
            throw new ObjectNotFoundException();
        }
    }

    private void ensureFileNotExist(String objectName) {
        if (storageClient.isFileExist(objectName)) {
            throw new ObjectAlreadyExistException();
        }
    }
}
