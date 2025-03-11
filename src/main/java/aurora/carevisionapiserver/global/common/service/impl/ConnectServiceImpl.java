package aurora.carevisionapiserver.global.common.service.impl;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.common.service.ConnectService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ConnectServiceImpl implements ConnectService {
    @Override
    public void connectNurseToPatient(Patient patient, Nurse nurse) {
        patient.registerNurse(nurse);
        nurse.getPatients().add(patient);
    }
}
