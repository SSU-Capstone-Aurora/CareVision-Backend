package aurora.carevisionapiserver.global.util.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import aurora.carevisionapiserver.global.util.validation.validator.IsActivateNurseValidator;

@Constraint(validatedBy = IsActivateNurseValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsActivateNurse {
    String message() default "활성화되지 않은 유저입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
