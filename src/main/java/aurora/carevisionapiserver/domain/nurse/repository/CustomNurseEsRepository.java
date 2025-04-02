package aurora.carevisionapiserver.domain.nurse.repository;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;

public interface CustomNurseEsRepository {
    Slice<NurseDocument> findActiveNursesByNameAndDepartmentOrAll(
            String nurseName, Long departmentId, Long lastIdx, int size);
}
