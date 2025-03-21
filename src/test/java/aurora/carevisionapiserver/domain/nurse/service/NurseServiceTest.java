package aurora.carevisionapiserver.domain.nurse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.converter.NurseDocumentConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewPageResponse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseEsRepository;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;

class NurseServiceTest extends IntegrationTestSupport {

    @Autowired NurseService nurseService;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired BedRepository bedRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired NurseEsRepository nurseEsRepository;

    @AfterEach
    void tearDown() {
        nurseEsRepository.deleteAll();
        nurseRepository.deleteAllInBatch();
        bedRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
    }

    @DisplayName("관리자의 부서에서 근무하는 활성화된 간호사를 간호사 이름으로 검색하면 첫 페이지 결과를 반환한다.")
    @Test
    void searchActiveNurses() {
        // given
        Department department = createDepartment();
        Admin admin = createAdmin(department);
        Nurse nurse1 = createNurse("오로라", "nurse1", department, true);
        Nurse nurse2 = createNurse("최로라", "nurse2", department, true);
        Nurse nurse3 = createNurse("홍로라", "nurse3", department, false);

        nurseEsRepository.saveAll(
                NurseDocumentConverter.toNurseDocumentList(List.of(nurse1, nurse2, nurse3)));

        PageRequest request = new PageRequest(-1L, 2);
        String nurseName = "로라";

        // when
        NursePreviewPageResponse response =
                nurseService.searchActiveNurses(admin, request, nurseName);

        // then
        assertThat(response.getNurseList())
                .hasSize(2)
                .extracting("name", "id")
                .containsExactlyInAnyOrder(tuple("오로라", "nurse1"), tuple("최로라", "nurse2"));
        assertThat(response.getNextCursor()).isEqualTo(nurse2.getId());
        assertThat(response.isHasNext()).isFalse();
    }

    // TODO : 조회 API와 통합되었는지 확인하는 테스트 추가

    private Admin createAdmin(Department department) {
        return Admin.builder().department(department).username("admin").build();
    }

    private Department createDepartment() {
        Hospital hospital = createHospital();
        Department department = Department.builder().name("department").hospital(hospital).build();
        return departmentRepository.save(department);
    }

    private Hospital createHospital() {
        Hospital hospital = Hospital.builder().name("hospital").build();
        return hospitalRepository.save(hospital);
    }

    private Nurse createNurse(
            String name, String username, Department department, boolean isActivated) {
        Nurse nurse =
                Nurse.builder()
                        .name(name)
                        .department(department)
                        .username(username)
                        .isActivated(isActivated)
                        .build();
        return nurseRepository.save(nurse);
    }
}
