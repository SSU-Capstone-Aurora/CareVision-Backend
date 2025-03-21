package aurora.carevisionapiserver.domain.nurse.repository;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;

public interface CustomNurseEsRepository {
    Slice<NurseDocument> findActiveNursesByNameAndAdminOrAll(
            String nurseName, Admin admin, Long lastIdx, int size);
}
