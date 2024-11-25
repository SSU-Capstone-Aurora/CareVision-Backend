package aurora.carevisionapiserver.global.util.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.auth.dto.request.AuthRequest.LoginRequest;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.validation.annotation.IsActivateNurse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IsActivateNurseValidator
        implements ConstraintValidator<IsActivateNurse, LoginRequest> {

    private final NurseRepository nurseRepository;

    @Override
    public boolean isValid(LoginRequest value, ConstraintValidatorContext context) {
        String username = value.getUsername();
        if (username != null) {
            Nurse nurse = nurseRepository.findByUsername(username).orElse(null);

            if (nurse == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                String.valueOf(ErrorStatus.INVALID_CREDENTIALS))
                        .addConstraintViolation();
                return false;
            }

            if (!nurse.isActivated()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                String.valueOf(ErrorStatus.USER_NOT_ACTIVATED))
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
