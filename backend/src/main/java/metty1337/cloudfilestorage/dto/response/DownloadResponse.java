package metty1337.cloudfilestorage.dto.response;

import metty1337.cloudfilestorage.storage.StreamingBody;

public record DownloadResponse(StreamingBody body, String contentType) {
}
