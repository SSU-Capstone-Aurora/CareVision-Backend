package aurora.carevisionapiserver.domain.patient.service;

import java.util.List;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;

public interface PatientService {
    List<Patient> searchPatient(String patientName);

    Patient getPatient(Long patientId);

    List<Patient> getPatients(Nurse nurse);

    List<Patient> getPatients(Long adminId);

    void deletePatient(Long patientId);

    Patient createPatient(PatientCreateRequest patientCreateRequest);
}
