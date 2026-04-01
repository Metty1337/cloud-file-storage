package metty1337.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.constants.ObjectType;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageFileResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.*;
import metty1337.cloudfilestorage.storage.ObjectData;
import metty1337.cloudfilestorage.storage.StorageClient;
import metty1337.cloudfilestorage.storage.StoragePathResolver;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public StorageObjectResponse getObjectData(String path, long userId) {
        String objectName = StoragePathResolver.getObjectName(path, userId);
        if (StoragePathResolver.isFile(path)) {

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
        } else if (storageClient.isDirectoryExist(objectName)) {
            String filePath = StoragePathResolver.getParentPath(path);
            String name = StoragePathResolver.getDirectoryName(path);

            return new StorageDirectoryResponse(
                    filePath,
                    name,
                    ObjectType.DIRECTORY.name()
            );
        }
        throw new StorageAccessException();
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
        return new InputStreamResource(storageClient.getObject(objectName));
    }

    public void downloadFolder(String path, long userId, OutputStream outputStream) {
        String folderName = StoragePathResolver.getObjectName(path, userId);
        ensureDirectoryExist(folderName);

        downloadAsZip(outputStream, folderName);
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

    public List<StorageObjectResponse> searchObject(String query, long userId) {
        String userDirectory = StoragePathResolver.getUserDirectory(userId);
        var objects = storageClient.listObjectsByPrefix(userDirectory, true);

        List<StorageObjectResponse> responses = new ArrayList<>();
        Set<String> directories = new LinkedHashSet<>();

        for (var object : objects) {
            try {
                String objectPath = object.get().objectName();

                collectParentDirectory(objectPath, directories);

                if (!objectPath.contains(query)) {
                    continue;
                }

                StorageFileResponse response = new StorageFileResponse(
                        StoragePathResolver.getViewFilePath(objectPath, userId),
                        StoragePathResolver.getFileName(objectPath),
                        object.get().size(),
                        ObjectType.FILE.name()
                );
                responses.add(response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (String directory : directories) {

            if (!directory.contains(query)) {
                continue;
            }

            StorageDirectoryResponse response = new StorageDirectoryResponse(
                    StoragePathResolver.getViewFilePath(directory, userId),
                    StoragePathResolver.getDirectoryName(directory),
                    ObjectType.DIRECTORY.name()
            );
            responses.add(response);
        }
        return responses;
    }

    public List<StorageObjectResponse> getDirectoryContents(String path, long userId) {
        String directoryName = StoragePathResolver.getObjectName(path, userId);
        ensureDirectoryExist(directoryName);

        var objects = storageClient.listObjectsByPrefix(directoryName, false);

        List<StorageObjectResponse> responses = new ArrayList<>();
        for (var object : objects) {

            try {
                String objectName = object.get().objectName();

                if (StoragePathResolver.isFile(objectName)) {
                    StorageFileResponse response = new StorageFileResponse(
                            StoragePathResolver.getViewFilePath(objectName, userId),
                            StoragePathResolver.getFileName(objectName),
                            object.get().size(),
                            ObjectType.FILE.name()
                    );
                    responses.add(response);
                } else {
                    StorageDirectoryResponse response = new StorageDirectoryResponse(
                            StoragePathResolver.getViewFilePath(objectName, userId),
                            StoragePathResolver.getDirectoryName(objectName),
                            ObjectType.DIRECTORY.name()
                    );
                    responses.add(response);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return responses;
    }

    public StorageDirectoryResponse createDirectory(String path, long userId) {
        String directoryName = StoragePathResolver.getObjectName(path, userId);

        String parentDirectory = StoragePathResolver.getParentPath(directoryName);
        ensureDirectoryExist(parentDirectory);

        storageClient.createDirectory(directoryName);

        return new StorageDirectoryResponse(
                StoragePathResolver.getViewFilePath(directoryName, userId),
                StoragePathResolver.getDirectoryName(directoryName),
                ObjectType.DIRECTORY.name()
        );
    }

    private void collectParentDirectory(String objectPath, Set<String> directories) {
        String parentDirectory = StoragePathResolver.getParentDirectory(objectPath) + "/";
        directories.add(parentDirectory);
    }

    private void deleteDirectory(String objectName) {
        ensureDirectoryExist(objectName);
        storageClient.removeDirectory(objectName);
    }

    private void downloadAsZip(OutputStream outputStream, String folderName) {
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            var objects = storageClient.listObjectsByPrefix(folderName, true);

            for (var object : objects) {
                String objectName = object.get().objectName();

                String entryName = objectName.substring(folderName.length());
                zipOut.putNextEntry(new ZipEntry(entryName));

                try (InputStream inputStream = storageClient.getObject(objectName)) {
                    inputStream.transferTo(zipOut);
                }
                zipOut.closeEntry();
            }
        } catch (Exception e) {
            throw new StorageDownloadingException(e);
        }
    }

    private static boolean isDirectoryFiles(List<MultipartFile> files) {
        return files.size() > 1;
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
