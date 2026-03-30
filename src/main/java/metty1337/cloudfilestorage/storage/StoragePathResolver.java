package metty1337.cloudfilestorage.storage;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class StoragePathResolver {

    public static @NonNull String getResourceName(String path, long userId) {
        return getUserDirectory(userId) + path;
    }

    public static @NonNull String getUserDirectory(long userId) {
        return "user-%s-files/".formatted(userId);
    }

    public static boolean isFile(String path) {
        return !path.endsWith("/");
    }

    public static @NonNull String getDirectoryName(String path) {
        String withoutTrailingSlash = path.substring(0, path.length() - 1);
        return withoutTrailingSlash.substring(withoutTrailingSlash.lastIndexOf('/') + 1);
    }

    public static @NonNull String getFileName(String resourceName) {
        return resourceName.substring(resourceName.lastIndexOf('/') + 1);
    }

    public static @NonNull String getViewFilePath(String resourceName, long userId) {
        String userDirectory = getUserDirectory(userId);
        String withoutUserDir = resourceName.substring(userDirectory.length());
        int lastSlash = withoutUserDir.lastIndexOf('/');
        return withoutUserDir.substring(0, lastSlash + 1);
    }
}
