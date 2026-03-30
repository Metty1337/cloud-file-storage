package metty1337.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import metty1337.cloudfilestorage.dto.request.validation.ValidConsistentObjectType;

@ValidConsistentObjectType
public record StorageMoveRequest(
        @NotBlank String from,
        @NotBlank String to
) {
}
