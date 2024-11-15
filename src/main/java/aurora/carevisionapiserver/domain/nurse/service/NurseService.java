package aurora.carevisionapiserver.domain.nurse.service;

import java.util.List;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface NurseService {
    boolean existsByNurseId(Long value);

    Nurse getNurse(String username);

    Nurse getActiveNurse(Long nurseId);

    Nurse getInactiveNurse(Long nurseId);

    List<Nurse> getActiveNurses(Admin admin);

    List<Nurse> getInactiveNurses(Admin admin);

    List<Nurse> searchNurse(String nurseName);

    Nurse createNurse(
            NurseCreateRequest nurseCreateRequest, Hospital hospital, Department department);

    boolean isUsernameDuplicated(String username);

    void activateNurse(Long nurseId);

    void deleteInactiveNurse(Long nurseId);

    long getNurseRegisterRequestCount(Admin admin);

    void deleteActiveNurse(Long nurseId);

    void connectPatient(Nurse nurse, Patient patient);
}
