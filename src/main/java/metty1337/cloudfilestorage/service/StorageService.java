package metty1337.cloudfilestorage.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import metty1337.cloudfilestorage.config.minio.MinioProperties;
import metty1337.cloudfilestorage.dto.response.StorageResponse;
import metty1337.cloudfilestorage.exception.ResourceNotFound;
import metty1337.cloudfilestorage.exception.StorageAccessException;
import metty1337.cloudfilestorage.exception.StorageUploadException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private static final int AUTO_DETECT_PART_SIZE_VALUE = -1;
    private static final String CONTENT_TYPE_FILE = "FILE";
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public StorageResponse uploadFile(MultipartFile multipartFile, String path, long userId) {
        String resourceName = getResourceName(path, userId);

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

            return new StorageResponse(
                    path,
                    multipartFile.getOriginalFilename(),
                    fileSize,
                    CONTENT_TYPE_FILE
            );
        } catch (Exception e) {
            throw new StorageUploadException(e);
        }
    }

    public StorageResponse getResourceData(String path, long userId) {
        String resourceName = getResourceName(path, userId);

        try {
            StatObjectResponse objectStat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(resourceName)
                            .build()
            );

            String filePath = getFilePath(objectStat.object(), userId);
            String name = getFileName(objectStat.object());
            long size = objectStat.size();

            return new StorageResponse(
                    filePath,
                    name,
                    size,
                    CONTENT_TYPE_FILE
            );
        } catch (ErrorResponseException e) {
            throw new ResourceNotFound(e);
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

    private static @NonNull String getFileName(String resourceName) {
        return resourceName.substring(resourceName.lastIndexOf('/') + 1);
    }

    private static @NonNull String getFilePath(String resourceName, long userId) {
        String userDirectory = getUserDirectory(userId);
        String withoutUserDir = resourceName.substring(userDirectory.length());
        int lastSlash = withoutUserDir.lastIndexOf('/');
        return withoutUserDir.substring(0, lastSlash + 1);
    }
}
