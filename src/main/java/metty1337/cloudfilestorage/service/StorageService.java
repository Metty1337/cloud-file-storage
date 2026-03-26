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

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public UploadResponse uploadFile(MultipartFile multipartFile, String path) {
        String fileName = multipartFile.getOriginalFilename();

        try {
            long fileSize = multipartFile.getSize();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucket().name())
                            .object(path + fileName)
                            .stream(multipartFile.getInputStream(), fileSize, -1)
                            .contentType(multipartFile.getContentType())
                            .build()
            );

            return new UploadResponse(
                    path,
                    fileName,
                    fileSize,
                    "FILE"
            );
        } catch (Exception e) {
            throw new StorageUploadException(e);
        }
    }
}
