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
    public @NonNull InputStreamResource getResource(String resourceName) {
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

    @Override
    public ObjectData getStatResponse(String resourceName) {
        try {
            StatObjectResponse objectResponse = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .build()
            );

            return new ObjectData(
                    objectResponse.object(),
                    objectResponse.size()
            );
        } catch (ErrorResponseException e) {
            throw new ResourceNotFoundException(e);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
    }

    @Override
    public void upload(String resourceName, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .stream(inputStream, size, AUTO_DETECT_PART_SIZE_VALUE)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageUploadException(e);
        }
    }

    @Override
    public void copyObject(String oldResourceName, String newResourceName) {
        try {
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
        } catch (Exception e) {
            throw new StorageCopyException(e);
        }
    }

    @Override
    public void removeFile(String resourceName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageDeleteException(e);
        }
    }

    @Override
    public boolean isFileExist(String resourceName) {
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
    public void moveFile(String oldResourceName, String newResourceName) {
        try {
            copyObject(oldResourceName, newResourceName);
            removeFile(oldResourceName);
        } catch (Exception e) {
            throw new StorageMoveException(e);
        }
    }

    @Override
    public void moveDirectory(String oldResourceName, String newResourceName) {
        try {
            Iterable<Result<Item>> oldObjects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .prefix(oldResourceName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> oldObject : oldObjects) {
                String oldName = oldObject.get().objectName();
                String newName = replaceFileNamePrefix(oldName, oldResourceName, newResourceName);

                copyObject(oldName, newName);
                removeFile(oldName);
            }
        } catch (Exception e) {
            throw new StorageMoveException(e);
        }
    }

    @Override
    public long getFileSize(String resourceName) {
        ObjectData objectData = getStatResponse(resourceName);
        return objectData.size();
    }

    private String replaceFileNamePrefix(String fileName, String oldPrefix, String newPrefix) {
        String withoutOldPrefix = fileName.substring(oldPrefix.length());
        return newPrefix + withoutOldPrefix;
    }
}
