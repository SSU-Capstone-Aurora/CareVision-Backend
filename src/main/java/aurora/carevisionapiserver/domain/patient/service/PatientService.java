package aurora.carevisionapiserver.domain.patient.service;

import java.util.List;

import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface PatientService {
    List<Patient> searchPatient(String patientName);
}
