package metty1337.cloudfilestorage.storage;

import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageClient {
    @NonNull InputStreamResource getResource(String resourceName);

    ObjectData getStatResponse(String resourceName);

    void upload(String resourceName, InputStream inputStream, long size, String contentType);

    void copyObject(String oldResourceName, String newResourceName);

    void removeFile(String resourceName);

    boolean isFileExist(String resourceName);

    boolean isDirectoryExist(String directoryName);

    void moveFile(String oldResourceName, String newResourceName);

    void moveDirectory(String oldResourceName, String newResourceName);

    long getFileSize(String resourceName);
}
