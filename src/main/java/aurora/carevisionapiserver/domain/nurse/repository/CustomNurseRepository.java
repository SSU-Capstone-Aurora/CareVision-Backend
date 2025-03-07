package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;

public interface CustomNurseRepository {
    List<Nurse> findInactiveNursesByAdmin(Admin admin);

    long countInactiveNursesByAdmin(Admin admin);

    Slice<Nurse> findActiveNursesByAdmin(Admin admin, Long lastIdx, int size);
}
