package metty1337.cloudfilestorage.storage;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class StoragePathResolver {

    public static @NonNull String getObjectName(String path, long userId) {
        return getUserDirectory(userId) + path;
    }

    public static boolean isFile(String path) {
        return !path.endsWith("/");
    }

    public static @NonNull String getDirectoryName(String path) {
        String withoutTrailingSlash = path.substring(0, path.length() - 1);
        return withoutTrailingSlash.substring(withoutTrailingSlash.lastIndexOf('/') + 1);
    }

    public static @NonNull String getFileName(String objectName) {
        return objectName.substring(objectName.lastIndexOf('/') + 1);
    }

    public static @NonNull String getParentDirectory(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    public static @NonNull String getViewFilePath(String objectName, long userId) {
        String userDirectory = getUserDirectory(userId);
        String withoutUserDir = objectName.substring(userDirectory.length());
        int lastSlash = withoutUserDir.lastIndexOf('/');
        return withoutUserDir.substring(0, lastSlash + 1);
    }

    public static @NonNull String getParentPath(String objectName) {
        String dirName = getDirectoryName(objectName);
        return objectName.substring(0, objectName.length() - dirName.length() - 1);
    }

    private static @NonNull String getUserDirectory(long userId) {
        return "user-%s-files/".formatted(userId);
    }
}
