package metty1337.cloudfilestorage.service;

import metty1337.cloudfilestorage.dto.request.FileUploadData;
import metty1337.cloudfilestorage.dto.response.DownloadResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageDirectoryResponse;
import metty1337.cloudfilestorage.dto.response.storage.StorageObjectResponse;

import java.util.List;

public interface StorageService {
    StorageObjectResponse uploadObject(List<FileUploadData> files, String path, long userId);

    StorageObjectResponse getObjectData(String path, long userId);

    void deleteObject(String path, long userId);

    DownloadResponse downloadObject(String path, long userId);

    StorageObjectResponse moveObject(String from, String to, long userId);

    List<StorageObjectResponse> searchObject(String query, long userId);

    List<StorageObjectResponse> getDirectoryContents(String path, long userId);

    StorageDirectoryResponse createDirectory(String path, long userId);
}
