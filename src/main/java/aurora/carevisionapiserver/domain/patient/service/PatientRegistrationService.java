package aurora.carevisionapiserver.domain.patient.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.service.BedService;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.converter.PatientDocumentConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest;
import aurora.carevisionapiserver.domain.patient.repository.PatientEsRepository;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.global.util.PatientValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientRegistrationService {
    private final BedService bedService;
    private final PatientRepository patientRepository;
    private final PatientEsRepository patientEsRepository;
    private final PatientValidator patientValidator;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Patient createPatient(
            PatientRequest.PatientCreateRequest patientCreateRequest, Department department) {
        Patient patient = registerPatientToBed(patientCreateRequest, department);
        savePatient(patient);

        return patient;
    }

    private Patient registerPatientToBed(
            PatientRequest.PatientCreateRequest patientCreateRequest, Department department) {
        Bed bed = bedService.findBed(patientCreateRequest.getBed());

        patientValidator.validatePatientCode(patientCreateRequest.getCode());
        Patient patient = PatientConverter.toPatient(patientCreateRequest, bed, department);

        bed.registerPatient(patient);

        return patient;
    }

    private void savePatient(Patient patient) {
        patientRepository.save(patient);
        patientEsRepository.save(PatientDocumentConverter.toPatientDocument(patient));
    }
}
