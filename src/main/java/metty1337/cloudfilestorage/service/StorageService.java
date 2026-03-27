package metty1337.cloudfilestorage.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.config.minio.MinioProperties;
import metty1337.cloudfilestorage.dto.response.UploadResponse;
import metty1337.cloudfilestorage.exception.StorageUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageService {

    private static final int AUTO_DETECT_PART_SIZE_VALUE = -1;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public UploadResponse uploadFile(MultipartFile multipartFile, String path, long userId) {
        String userDirectory = "user-%s-files/".formatted(userId);
        String fileDirectory = userDirectory + path;

        try {
            long fileSize = multipartFile.getSize();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(fileDirectory)
                            .stream(multipartFile.getInputStream(), fileSize, AUTO_DETECT_PART_SIZE_VALUE)
                            .contentType(multipartFile.getContentType())
                            .build()
            );

            return new UploadResponse(
                    path,
                    multipartFile.getOriginalFilename(),
                    fileSize,
                    "FILE"
            );
        } catch (Exception e) {
            throw new StorageUploadException(e);
        }
    }
}
