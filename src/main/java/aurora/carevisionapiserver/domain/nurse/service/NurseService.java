package aurora.carevisionapiserver.domain.nurse.service;

import java.util.List;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;

public interface NurseService {
    boolean existsByNurseId(Long value);

    Nurse getNurse(String username);

    Nurse getActiveNurse(Long nurseId);

    Nurse getInActiveNurse(Long nurseId);

    List<Nurse> getActiveNurses(Admin admin);

    List<Nurse> getInActiveNurses(Admin admin);

    List<Nurse> searchNurse(String nurseName);

    Nurse createNurse(NurseCreateRequest nurseCreateRequest, Hospital hospital);

    boolean isUsernameDuplicated(String username);

    void activateNurse(Admin admin, Long nurseId);

    void deleteNurse(Admin admin, Long nurseId);
}
