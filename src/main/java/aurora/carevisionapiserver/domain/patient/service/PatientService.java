package aurora.carevisionapiserver.domain.patient.service;

import java.util.List;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface PatientService {
    List<Patient> searchPatient(String patientName);

    List<Patient> getPatients(Nurse nurse);

    List<Patient> getPatients(Long adminId);

    String registerNurse(Nurse nurse, String patientId);

    void deletePatient(Long patientId);
}
