package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;
import java.util.Optional;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;

public interface CustomNurseRepository {
    List<Nurse> findActiveNursesByAdmin(Admin admin);

    List<Nurse> findInActiveNursesByAdmin(Admin admin);

    Optional<Nurse> findActiveNurseById(Long nurseId);

    Optional<Nurse> findInActiveNurseById(Long nurseId);
}
