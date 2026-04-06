package metty1337.cloudfilestorage.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.constants.ObjectType;
import metty1337.cloudfilestorage.dto.request.FileUploadData;
import metty1337.cloudfilestorage.dto.response.DownloadResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageFileResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.ObjectAlreadyExistException;
import metty1337.cloudfilestorage.exception.ObjectNotFoundException;
import metty1337.cloudfilestorage.exception.storage.StorageAccessException;
import metty1337.cloudfilestorage.exception.storage.StorageDownloadingException;
import metty1337.cloudfilestorage.exception.storage.StorageSearchingException;
import metty1337.cloudfilestorage.service.StorageService;
import metty1337.cloudfilestorage.storage.ObjectData;
import metty1337.cloudfilestorage.storage.StorageClient;
import metty1337.cloudfilestorage.storage.StoragePathResolver;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageClient storageClient;

    @Override
    public StorageObjectResponse uploadObject(List<FileUploadData> files, String path, long userId) {
        Set<String> createdDirectories = new HashSet<>();
        String basePath = StoragePathResolver.getObjectName(path, userId);

        for (FileUploadData file : files) {
            String objectName = basePath + file.filename();

            createParentDirectories(objectName, basePath, createdDirectories);
            ensureFileNotExist(objectName);
            uploadFile(file, objectName);
        }

        FileUploadData object = files.getFirst();
        if (isDirectoryFiles(files)) {
            return new StorageDirectoryResponse(
                    path,
                    StoragePathResolver.getParentDirectory(Objects.requireNonNull(object.filename())) + "/",
                    ObjectType.DIRECTORY
            );
        } else {
            return new StorageFileResponse(
                    path,
                    object.filename(),
                    object.size(),
                    ObjectType.FILE
            );
        }
    }

    @Override
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
                    ObjectType.FILE
            );
        } else {
            ensureDirectoryExist(objectName);
            String filePath = StoragePathResolver.getParentPath(path);
            String name = StoragePathResolver.getDirectoryName(path);

            return new StorageDirectoryResponse(
                    filePath,
                    name + "/",
                    ObjectType.DIRECTORY
            );
        }
    }

    @Override
    public void deleteObject(String path, long userId) {
        String objectName = StoragePathResolver.getObjectName(path, userId);
        if (StoragePathResolver.isFile(objectName)) {
            deleteFile(objectName);
        } else {
            deleteDirectory(objectName);
        }
    }

    @Override
    public DownloadResponse downloadObject(String path, long userId) {
        if (StoragePathResolver.isFile(path)) {
            String objectName = StoragePathResolver.getObjectName(path, userId);
            ensureFileExists(objectName);
            return new DownloadResponse(
                    output -> {
                        try (InputStream inputStream = storageClient.getObject(objectName)) {
                            inputStream.transferTo(output);
                        }
                    },
                    MediaType.APPLICATION_OCTET_STREAM_VALUE
            );
        }

        String folderName = StoragePathResolver.getObjectName(path, userId);
        ensureDirectoryExist(folderName);
        return new DownloadResponse(
                output -> downloadAsZip(output, folderName),
                "application/zip"
        );
    }

    @Override
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
                    ObjectType.FILE
            );
        }
        ensureDirectoryExist(oldObjectName);
        String newObjectName = StoragePathResolver.getObjectName(to, userId);
        storageClient.moveDirectory(oldObjectName, newObjectName);

        return new StorageDirectoryResponse(
                StoragePathResolver.getViewFilePath(newObjectName, userId),
                StoragePathResolver.getDirectoryName(newObjectName) + "/",
                ObjectType.DIRECTORY
        );
    }

    @Override
    public List<StorageObjectResponse> searchObject(String query, long userId) {
        String userDirectory = StoragePathResolver.getUserDirectory(userId);
        var objects = storageClient.listObjectsByPrefix(userDirectory, true);

        List<StorageObjectResponse> responses = new ArrayList<>();
        Set<String> directories = new LinkedHashSet<>();

        for (var object : objects) {
            try {
                var item = object.get();
                String objectPath = item.objectName();

                if (StoragePathResolver.isFile(objectPath)) {
                    collectParentDirectory(objectPath, directories);

                    if (!objectPath.contains(query)) {
                        continue;
                    }

                    StorageFileResponse response = new StorageFileResponse(
                            StoragePathResolver.getViewFilePath(objectPath, userId),
                            StoragePathResolver.getFileName(objectPath),
                            item.size(),
                            ObjectType.FILE
                    );
                    responses.add(response);
                }
            } catch (Exception e) {
                throw new StorageSearchingException(e);
            }
        }

        for (String directory : directories) {

            if (!directory.contains(query)) {
                continue;
            }

            StorageDirectoryResponse response = new StorageDirectoryResponse(
                    StoragePathResolver.getViewFilePath(directory, userId),
                    StoragePathResolver.getDirectoryName(directory) + "/",
                    ObjectType.DIRECTORY
            );
            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<StorageObjectResponse> getDirectoryContents(String path, long userId) {
        String directoryName = StoragePathResolver.getObjectName(path, userId);
        List<StorageObjectResponse> responses = new ArrayList<>();
        if (!storageClient.isDirectoryExist(directoryName)) {
            return responses;
        }

        var objects = storageClient.listObjectsByPrefix(directoryName, false);

        String userDirectory = StoragePathResolver.getUserDirectory(userId);
        for (var object : objects) {

            try {
                var item = object.get();
                String objectName = item.objectName();

                if (StoragePathResolver.isFile(objectName)) {
                    StorageFileResponse response = new StorageFileResponse(
                            StoragePathResolver.getViewFilePath(objectName, userId),
                            StoragePathResolver.getFileName(objectName),
                            item.size(),
                            ObjectType.FILE
                    );
                    responses.add(response);
                } else {
                    if (objectName.equals(directoryName)) {
                        continue;
                    }

                    if (objectName.equals(userDirectory)) {
                        continue;
                    }

                    String directoryPath = StoragePathResolver.getDirectoryName(objectName);

                    StorageDirectoryResponse response = new StorageDirectoryResponse(
                            path,
                            directoryPath + "/",
                            ObjectType.DIRECTORY
                    );
                    responses.add(response);
                }
            } catch (Exception e) {
                throw new StorageAccessException(e);
            }
        }
        return responses;
    }

    @Override
    public StorageDirectoryResponse createDirectory(String path, long userId) {
        String directoryName = StoragePathResolver.getObjectName(path, userId);
        ensureDirectoryNotExist(directoryName);

        String parentDirectory = StoragePathResolver.getParentPath(directoryName);
        if (StoragePathResolver.countSlashes(path) > 1) {
            ensureDirectoryExist(parentDirectory);
        }

        storageClient.createDirectory(directoryName);

        return new StorageDirectoryResponse(
                StoragePathResolver.getViewFilePath(parentDirectory, userId),
                StoragePathResolver.getDirectoryName(directoryName) + "/",
                ObjectType.DIRECTORY
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

    private static boolean isDirectoryFiles(List<FileUploadData> files) {
        return files.size() > 1;
    }

    private void createParentDirectories(String objectName, String basePath, Set<String> createdDirectories) {
        for (String dirPath : StoragePathResolver.getIntermediateDirectories(objectName, basePath)) {
            if (createdDirectories.add(dirPath) && !storageClient.isDirectoryExist(dirPath)) {
                storageClient.createDirectory(dirPath);
            }
        }
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

    private void ensureDirectoryNotExist(String objectName) {
        if (storageClient.isDirectoryExist(objectName)) {
            throw new ObjectAlreadyExistException();
        }
    }

    private void uploadFile(FileUploadData file, String objectName) {
        storageClient.uploadFile(objectName, file.content(), file.size(), file.contentType());
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
