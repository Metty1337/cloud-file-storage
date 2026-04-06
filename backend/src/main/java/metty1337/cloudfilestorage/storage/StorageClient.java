package metty1337.cloudfilestorage.storage;

import org.jspecify.annotations.NonNull;

import java.io.InputStream;
import java.util.List;

public interface StorageClient {
    @NonNull InputStream getObject(String objectName);

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

    List<ObjectData> listObjectsByPrefix(String prefix, boolean recursive);

    void createDirectory(String directoryName);
}
