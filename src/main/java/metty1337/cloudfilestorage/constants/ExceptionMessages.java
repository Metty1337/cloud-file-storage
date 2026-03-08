package metty1337.cloudfilestorage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionMessages {
    USER_NOT_FOUND_EXCEPTION("User not found: %s"),
    BAD_CREDENTIALS_EXCEPTION("Bad credentials"),
    USER_DATA_INTEGRITY_VIOLATION_EXCEPTION("Username is already in use");

    private final String message;
}
