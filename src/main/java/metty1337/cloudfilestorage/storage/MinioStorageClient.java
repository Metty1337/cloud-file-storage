package metty1337.cloudfilestorage.storage;


import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.config.minio.MinioProperties;
import metty1337.cloudfilestorage.exception.*;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class MinioStorageClient implements StorageClient {

    private static final int AUTO_DETECT_PART_SIZE_VALUE = -1;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public @NonNull InputStreamResource getObject(String objectName) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(objectName)
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new StorageDownloadingException(e);
        }
    }

    @Override
    public ObjectData getObjectData(String objectName) {
        try {
            StatObjectResponse objectResponse = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(objectName)
                            .build()
            );

            return new ObjectData(
                    objectResponse.object(),
                    objectResponse.size()
            );
        } catch (ErrorResponseException e) {
            throw new ObjectNotFoundException(e);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

    @Override
    public void uploadFile(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(objectName)
                            .stream(inputStream, size, AUTO_DETECT_PART_SIZE_VALUE)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageUploadException(e);
        }
    }

    @Override
    public void copyFile(String oldObjectName, String newObjectName) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(newObjectName)
                            .source(
                                    CopySource.builder()
                                            .bucket(minioProperties.bucket().name())
                                            .object(oldObjectName)
                                            .build()
                            )
                            .build()
            );
        } catch (Exception e) {
            throw new StorageCopyingException(e);
        }
    }

    @Override
    public void removeFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageObjectDeletionException(e);
        }
    }

    @Override
    public void removeDirectory(String directoryName) {
        Iterable<Result<Item>> directoryObjects = listObjectsByPrefix(directoryName);
        for (Result<Item> directoryObject : directoryObjects) {
            try {
                removeFile(directoryObject.get().objectName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isFileExist(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

    @Override
    public boolean isDirectoryExist(String directoryName) {
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

    @Override
    public void moveFile(String oldObjectName, String newObjectName) {
        try {
            copyFile(oldObjectName, newObjectName);
            removeFile(oldObjectName);
        } catch (Exception e) {
            throw new StorageMovementException(e);
        }
    }

    @Override
    public void moveDirectory(String oldObjectName, String newObjectName) {
        try {
            Iterable<Result<Item>> oldObjects = listObjectsByPrefix(oldObjectName);

            for (Result<Item> oldObject : oldObjects) {
                String oldName = oldObject.get().objectName();
                String newName = replaceFileNamePrefix(oldName, oldObjectName, newObjectName);

                copyFile(oldName, newName);
                removeFile(oldName);
            }
        } catch (Exception e) {
            throw new StorageMovementException(e);
        }
    }

    @Override
    public long getFileSize(String objectName) {
        ObjectData objectData = getObjectData(objectName);
        return objectData.size();
    }

    private String replaceFileNamePrefix(String fileName, String oldPrefix, String newPrefix) {
        String withoutOldPrefix = fileName.substring(oldPrefix.length());
        return newPrefix + withoutOldPrefix;
    }

    private Iterable<Result<Item>> listObjectsByPrefix(String prefix) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.bucket().name())
                        .prefix(prefix)
                        .recursive(true)
                        .build()
        );
    }
}
