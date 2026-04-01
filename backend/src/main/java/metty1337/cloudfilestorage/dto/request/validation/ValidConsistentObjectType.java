package metty1337.cloudfilestorage.dto.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidConsistentObjectTypeValidator.class)
public @interface ValidConsistentObjectType {
    String message() default "Both 'from' and 'to' must be either files or directories";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
