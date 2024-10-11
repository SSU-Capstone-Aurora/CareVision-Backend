package aurora.carevisionapiserver.global.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ExistNurseValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistNurse {

    String message() default "존재하지 않는 간호사입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
