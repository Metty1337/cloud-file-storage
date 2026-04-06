package metty1337.cloudfilestorage.storage;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface StreamingBody {
    void writeTo(OutputStream outputStream) throws IOException;
}
