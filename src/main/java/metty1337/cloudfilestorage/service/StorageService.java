package metty1337.cloudfilestorage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.config.minio.MinioProperties;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageFileResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import metty1337.cloudfilestorage.exception.*;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private static final int AUTO_DETECT_PART_SIZE_VALUE = -1;
    private static final String RESOURCE_TYPE_FILE = "FILE";
    private static final String RESOURCE_TYPE_DIRECTORY = "DIRECTORY";
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public StorageObjectResponse uploadFile(MultipartFile multipartFile, String path, long userId) {
        String resourceName = getResourceName(path, userId) + multipartFile.getOriginalFilename();
        ensureFileNotExist(resourceName);

        try {
            long fileSize = multipartFile.getSize();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .stream(multipartFile.getInputStream(), fileSize, AUTO_DETECT_PART_SIZE_VALUE)
                            .contentType(multipartFile.getContentType())
                            .build()
            );
            ensureFileExists(resourceName);

            return new StorageFileResponse(
                    path,
                    multipartFile.getOriginalFilename(),
                    fileSize,
                    RESOURCE_TYPE_FILE
            );
        } catch (Exception e) {
            throw new StorageUploadException(e);
        }
    }

    public StorageObjectResponse getResourceData(String path, long userId) {
        String resourceName = getResourceName(path, userId);

        try {
            StatObjectResponse objectStat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .build()
            );

            String filePath = getViewFilePath(objectStat.object(), userId);
            String name = getFileName(objectStat.object());
            long size = objectStat.size();

            return new StorageFileResponse(
                    filePath,
                    name,
                    size,
                    RESOURCE_TYPE_FILE
            );
        } catch (ErrorResponseException e) {
            throw new ResourceNotFoundException(e);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

    public void deleteResource(String path, long userId) {
        String resourceName = getResourceName(path, userId);
        ensureFileExists(resourceName);


        try {
            removeObject(resourceName);
        } catch (Exception e) {
            throw new StorageDeleteException(e);
        }
    }

    public Resource downloadFile(String path, long userId) {
        String resourceName = getResourceName(path, userId);
        ensureFileExists(resourceName);


        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new StorageDownloadException(e);
        }
    }


    public StorageObjectResponse moveResource(String from, String to, long userId) {
        String oldResourceName = getResourceName(from, userId);

        if (isFile(oldResourceName)) {
            ensureFileExists(oldResourceName);

            String newResourceName = getResourceName(to, userId);
            ensureFileNotExist(newResourceName);

            try {
                copyObject(oldResourceName, newResourceName);
                removeObject(oldResourceName);

                String newFilePath = getViewFilePath(newResourceName, userId);
                String newFileName = getFileName(newResourceName);
                long size = getFileSize(newResourceName);


                return new StorageFileResponse(
                        newFilePath,
                        newFileName,
                        size,
                        RESOURCE_TYPE_FILE
                );
            } catch (Exception e) {
                throw new StorageMoveException(e);
            }
        }
        ensureDirectoryExist(oldResourceName);

        try {
            String newResourcePrefix = getResourceName(to, userId);
            Iterable<Result<Item>> oldObjects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .prefix(oldResourceName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> oldObject : oldObjects) {
                String oldName = oldObject.get().objectName();
                String newName = replaceFileNamePrefix(oldName, oldResourceName, newResourcePrefix);

                copyObject(oldName, newName);
                removeObject(oldName);
            }

            return new StorageDirectoryResponse(
                    newResourcePrefix,
                    getDirectoryName(newResourcePrefix),
                    RESOURCE_TYPE_DIRECTORY
            );
        } catch (Exception e) {
            throw new StorageMoveException(e);
        }
    }

    private void ensureDirectoryNotExist(String newResourcePrefix) {
        if (isDirectoryExist(newResourcePrefix)) {
            throw new ResourceAlreadyExist();
        }
    }

    private void ensureDirectoryExist(String oldResourceName) {
        if (!isDirectoryExist(oldResourceName)) {
            throw new ResourceNotFoundException();
        }
    }

    private boolean isDirectoryExist(String directoryName) {
        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.bucket().name())
                        .prefix(directoryName)
                        .recursive(true)
                        .maxKeys(1)
                        .build()
        );
        return objects != null && objects.iterator().hasNext();
    }

    private void copyObject(String oldResourceName, String newResourceName) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(minioProperties.bucket().name())
                        .object(newResourceName)
                        .source(
                                CopySource.builder()
                                        .bucket(minioProperties.bucket().name())
                                        .object(oldResourceName)
                                        .build()
                        )
                        .build()
        );
    }

    private void removeObject(String resourceName) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.bucket().name())
                        .object(resourceName)
                        .build()
        );
    }

    private String replaceFileNamePrefix(String fileName, String oldPrefix, String newPrefix) {
        String withoutOldPrefix = fileName.substring(oldPrefix.length());
        return newPrefix + withoutOldPrefix;
    }

    private long getFileSize(String resourceName) {
        try {
            StatObjectResponse objectStat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .build()
            );
            return objectStat.size();

        } catch (ErrorResponseException e) {
            throw new ResourceNotFoundException(e);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

    private void ensureFileExists(String resourceName) {
        if (!resourceExists(resourceName)) {
            throw new ResourceNotFoundException();
        }
    }

    private void ensureFileNotExist(String resourceName) {
        if (resourceExists(resourceName)) {
            throw new ResourceAlreadyExist();
        }
    }

    private boolean resourceExists(String resourceName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

    private static @NonNull String getResourceName(String path, long userId) {
        return getUserDirectory(userId) + path;
    }

    private static @NonNull String getUserDirectory(long userId) {
        return "user-%s-files/".formatted(userId);
    }

    private static boolean isFile(String path) {
        return !path.endsWith("/");
    }

    private static @NonNull String getDirectoryName(String path) {
        String withoutTrailingSlash = path.substring(0, path.length() - 1);
        return withoutTrailingSlash.substring(withoutTrailingSlash.lastIndexOf('/') + 1);
    }

    private static @NonNull String getFileName(String resourceName) {
        return resourceName.substring(resourceName.lastIndexOf('/') + 1);
    }

    private static @NonNull String getViewFilePath(String resourceName, long userId) {
        String userDirectory = getUserDirectory(userId);
        String withoutUserDir = resourceName.substring(userDirectory.length());
        int lastSlash = withoutUserDir.lastIndexOf('/');
        return withoutUserDir.substring(0, lastSlash + 1);
    }
}
