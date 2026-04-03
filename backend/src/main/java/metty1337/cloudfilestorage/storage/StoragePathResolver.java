package metty1337.cloudfilestorage.storage;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

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

    public static long countSlashes(String s) {
        return s.chars().filter(c -> c == '/').count();
    }

    public static @NonNull String getUserDirectory(long userId) {
        return "user-%s-files/".formatted(userId);
    }

    public static @NonNull List<String> getIntermediateDirectories(String objectName, String basePath) {
        String relativePath = objectName.substring(basePath.length());
        String[] parts = relativePath.split("/");

        List<String> directories = new ArrayList<>();
        StringBuilder currentPath = new StringBuilder(basePath);
        for (int i = 0; i < parts.length - 1; i++) {
            currentPath.append(parts[i]).append("/");
            directories.add(currentPath.toString());
        }
        return directories;
    }
}
