package metty1337.cloudfilestorage.storage;

import io.minio.Result;
import io.minio.messages.Item;
import org.jspecify.annotations.NonNull;

import java.io.InputStream;

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

    Iterable<Result<Item>> listObjectsByPrefix(String prefix, boolean recursive);
}
