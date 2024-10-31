package aurora.carevisionapiserver.domain.nurse.service;

import java.util.List;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;

public interface NurseService {
    boolean existsByNurseId(Long value);

    Nurse getNurse(Long nurseId);

    List<Nurse> getActiveNurses(Long adminId);

    List<Nurse> getInActiveNurses(Long adminId);

    List<Nurse> searchNurse(String nurseName);

    Nurse createNurse(NurseCreateRequest nurseCreateRequest, Hospital hospital);

    boolean isUsernameDuplicated(String username);

    void activateNurse(Long adminId, Long nurseId);

    void deleteNurse(Long adminId, Long nurseId);
}
