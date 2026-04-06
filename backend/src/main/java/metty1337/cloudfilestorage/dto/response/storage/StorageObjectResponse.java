package metty1337.cloudfilestorage.dto.response.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import metty1337.cloudfilestorage.constants.ObjectType;

@Schema(description = "Storage object metadata", oneOf = {StorageFileResponse.class, StorageDirectoryResponse.class})
public sealed interface StorageObjectResponse permits StorageFileResponse, StorageDirectoryResponse {
    String path();

    String name();

    ObjectType type();
}
