package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomNurseEsRepositoryImpl implements CustomNurseEsRepository {
    private final NurseEsRepository nurseEsRepository;

    @Override
    public Slice<NurseDocument> findActiveNursesByNameAndDepartmentOrAll(
            String nurseName, Long departmentId, Long lastIdx, int size) {
        List<NurseDocument> nurses = fetchNurses(nurseName, departmentId, lastIdx);
        return createSlice(nurses, size);
    }

    private List<NurseDocument> fetchNurses(String nurseName, Long departmentId, Long lastIdx) {
        if (nurseName == null || nurseName.isEmpty()) {
            return nurseEsRepository
                    .findByIsActivatedTrueAndDepartmentIdAndNurseIdIsGreaterThanOrderByNurseId(
                            departmentId, lastIdx);
        }
        return nurseEsRepository
                .findByIsActivatedTrueAndNameAndDepartmentIdAndNurseIdIsGreaterThanOrderByNurseId(
                        nurseName, departmentId, lastIdx);
    }

    private Slice<NurseDocument> createSlice(List<NurseDocument> nurses, int size) {
        boolean hasNext = nurses.size() > size;
        if (hasNext) {
            nurses = nurses.subList(0, size);
        }
        return new SliceImpl<>(nurses, Pageable.unpaged(), hasNext);
    }
}
