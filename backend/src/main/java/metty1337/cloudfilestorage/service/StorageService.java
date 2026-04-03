package metty1337.cloudfilestorage.service;

import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;

public interface StorageService {
    StorageObjectResponse uploadObject(List<MultipartFile> files, String path, long userId);

    StorageObjectResponse getObjectData(String path, long userId);

    void deleteObject(String path, long userId);

    Resource downloadFile(String path, long userId);

    void downloadFolder(String path, long userId, OutputStream outputStream);

    StorageObjectResponse moveObject(String from, String to, long userId);

    List<StorageObjectResponse> searchObject(String query, long userId);

    List<StorageObjectResponse> getDirectoryContents(String path, long userId);

    StorageDirectoryResponse createDirectory(String path, long userId);
}
