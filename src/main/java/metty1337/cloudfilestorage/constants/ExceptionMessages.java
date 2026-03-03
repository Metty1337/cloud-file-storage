package metty1337.cloudfilestorage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionMessages {
    USER_NOT_FOUND_EXCEPTION("User not found: %s");

    private final String message;
}
