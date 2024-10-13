package aurora.carevisionapiserver.domain.patient.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    @Override
    public List<Patient> searchPatient(String patientName) {
        List<Patient> patients = patientRepository.searchByName(patientName);
        if (patients.size() == 0) throw new PatientException(ErrorStatus.PATIENT_NOT_FOUND);
        return patients;
    }
}
