package aurora.carevisionapiserver.global.util.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseLoginRequest;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.validation.annotation.IsActivateNurse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IsActivateNurseValidator
        implements ConstraintValidator<IsActivateNurse, NurseLoginRequest> {

    private final NurseRepository nurseRepository;

    @Override
    public boolean isValid(NurseLoginRequest value, ConstraintValidatorContext context) {
        String username = value.getUsername();
        if (username != null) {
            Nurse nurse = nurseRepository.findByUsername(username).orElse(null);

            if (nurse == null || !nurse.isActivated()) {
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
