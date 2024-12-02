package aurora.carevisionapiserver.global.util;

import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientValidator {
    private final PatientRepository patientRepository;

    public void validatePatientCode(String code) {
        if (patientRepository.existsByCode(code)) {
            throw new PatientException(ErrorStatus.PATIENT_DUPLICATED);
        }
    }
}
