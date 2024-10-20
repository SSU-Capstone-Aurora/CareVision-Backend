package aurora.carevisionapiserver.domain.nurse.service;

import java.util.List;
import java.util.Optional;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;

public interface NurseService {
    boolean existsByNurseId(Long value);

    Optional<Nurse> getNurse(Long nurseId);

    List<Nurse> getNurseList();

    List<Nurse> searchNurse(String nurseName);

    Nurse createNurse(NurseCreateRequest nurseCreateRequest, Hospital hospital);

    boolean isUsernameDuplicated(String username);
}
