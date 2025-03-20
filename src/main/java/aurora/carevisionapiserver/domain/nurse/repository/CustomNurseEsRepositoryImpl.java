package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomNurseEsRepositoryImpl implements CustomNurseEsRepository {
    private final NurseEsRepository nurseEsRepository;

    @Override
    public Slice<NurseDocument> findActiveNursesByNameAndAdmin(
            String nurseName, Admin admin, Long lastIdx, int size) {

        Long departmentId = admin.getDepartment().getId();
        List<NurseDocument> nurses =
                nurseEsRepository
                        .findByIsActivatedTrueAndNameAndDepartmentIdAndNurseIdIsGreaterThanOrderByCreatedAt(
                                nurseName, departmentId, lastIdx);

        boolean hasNext = nurses.size() > size;
        if (hasNext) {
            nurses = nurses.subList(0, size);
        }
        return new SliceImpl<>(nurses, Pageable.unpaged(), hasNext);
    }
}
