package metty1337.cloudfilestorage.storage;

import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

public interface StorageClient {
    @NonNull InputStreamResource getObject(String objectName);

    ObjectData getObjectData(String objectName);

    void uploadFile(String objectName, InputStream inputStream, long size, String contentType);

    void copyFile(String oldObjectName, String newObjectName);

    void removeFile(String objectName);

    void removeDirectory(String directoryName);

    boolean isFileExist(String objectName);

    boolean isDirectoryExist(String directoryName);

    void moveFile(String oldObjectName, String newObjectName);

    void moveDirectory(String oldObjectName, String newObjectName);

    long getFileSize(String objectName);
}
