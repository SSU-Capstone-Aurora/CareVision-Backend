package aurora.carevisionapiserver.global.common.service;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface ConnectService {
    void connectNurseToPatient(Patient patient, Nurse nurse);
}
