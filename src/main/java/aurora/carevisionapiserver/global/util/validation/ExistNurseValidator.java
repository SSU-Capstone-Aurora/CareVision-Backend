package aurora.carevisionapiserver.global.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistNurseValidator implements ConstraintValidator<ExistNurse, Long> {

    private final NurseService nurseService;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = nurseService.existsByNurseId(value);
        if (!isValid) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(
                            String.valueOf(ErrorStatus.NURSE_NOT_FOUND))
                    .addConstraintViolation();
        }
        return isValid;
    }
}
