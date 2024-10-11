package aurora.carevisionapiserver.domain.nurse.service;

import java.util.List;
import java.util.Optional;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;

public interface NurseService {
    boolean existsByNurseId(Long value);

    Optional<Nurse> getNurse(Long nurseId);

    List<Nurse> getNurseList();

    List<Nurse> searchNurse(String nurseName);
}