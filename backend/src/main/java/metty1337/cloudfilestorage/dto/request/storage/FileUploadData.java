package metty1337.cloudfilestorage.dto.request.storage;

import java.io.InputStream;

public record FileUploadData(String filename, InputStream content, long size, String contentType) {
}
