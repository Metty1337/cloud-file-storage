package metty1337.cloudfilestorage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionMessages {
    USER_NOT_FOUND_EXCEPTION("User not found: %s"),
    BAD_CREDENTIALS_EXCEPTION("Bad credentials"),
    USER_ALREADY_EXIST_EXCEPTION("Username is already in use"),
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION("Required parameters are missing"),
    INTERNAL_SERVER_ERROR("Internal server error");

    private final String message;
}
