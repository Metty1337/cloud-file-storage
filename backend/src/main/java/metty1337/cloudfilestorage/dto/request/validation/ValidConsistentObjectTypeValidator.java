package metty1337.cloudfilestorage.dto.request.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import metty1337.cloudfilestorage.dto.request.storage.StorageMoveRequest;

public class ValidConsistentObjectTypeValidator implements ConstraintValidator<ValidConsistentObjectType, StorageMoveRequest> {

    @Override
    public boolean isValid(StorageMoveRequest request, ConstraintValidatorContext context) {
        boolean fromIsDir = request.from().endsWith("/");
        boolean toIsDir = request.to().endsWith("/");

        return fromIsDir == toIsDir;
    }
}
