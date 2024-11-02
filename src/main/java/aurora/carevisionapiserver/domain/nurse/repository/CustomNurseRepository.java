package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;

public interface CustomNurseRepository {
    List<Nurse> findActiveNursesByAdmin(Admin admin);

    List<Nurse> findInactiveNursesByAdmin(Admin admin);

    long countInactiveNursesByAdmin(Admin admin);
}
