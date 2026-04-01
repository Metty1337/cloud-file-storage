package metty1337.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StorageSearchRequest(
        @NotBlank String query
) {
}
