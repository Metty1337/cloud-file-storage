package metty1337.cloudfilestorage.dto.response.storage;

public sealed interface StorageObjectResponse permits StorageFileResponse, StorageDirectoryResponse {
    String path();

    String name();

    String type();
}
