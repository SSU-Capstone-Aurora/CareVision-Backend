package aurora.carevisionapiserver.domain.nurse.service;

import java.util.Optional;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;

public interface NurseService {
    boolean existsByNurseId(Long value);

    Optional<Nurse> getNurse(Long nurseId);
}
