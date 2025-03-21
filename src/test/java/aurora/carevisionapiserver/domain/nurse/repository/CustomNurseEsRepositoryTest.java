package aurora.carevisionapiserver.domain.nurse.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.converter.NurseDocumentConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;

class CustomNurseEsRepositoryTest extends IntegrationTestSupport {
    @Autowired PatientRepository patientRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired BedRepository bedRepository;
    @Autowired CustomNurseEsRepositoryImpl customNurseEsRepository;
    @Autowired NurseEsRepository nurseEsRepository;

    @AfterEach
    void tearDown() {
        nurseEsRepository.deleteAll();
        patientRepository.deleteAllInBatch();
        nurseRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
        bedRepository.deleteAllInBatch();
    }

    @DisplayName("관리자가 속한 부서의 활성 간호사를 이름으로 검색하면, 첫 페이지 결과를 반환한다.")
    @Test
    void findActiveNursesByNameAndAdmin() {
        // given
        Department department = createDepartment();
        Admin admin = createAdmin(department);
        Nurse nurse1 = createNurse("오로라", "nurse1", department, true);
        Nurse nurse2 = createNurse("최로라", "nurse2", department, false);

        nurseEsRepository.saveAll(
                NurseDocumentConverter.toNurseDocumentList(List.of(nurse1, nurse2)));

        String nurseName = "로라";
        Long lastIdx = -1L;
        int size = 2;

        // when
        Slice<NurseDocument> response =
                customNurseEsRepository.findActiveNursesByNameAndAdminOrAll(
                        nurseName, admin, lastIdx, size);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.hasNext()).isFalse();
        assertThat(response).extracting("username").contains(nurse1.getUsername());
    }

    @DisplayName("마지막 조회된 간호사의 ID를 기준으로, 관리자의 부서에서 근무하는 활성화된 간호사를 다음 페이지로 조회한다.")
    @Test
    void findActiveNursesByNameAndAdminInNextCursor() {
        // given
        Department department = createDepartment();
        Admin admin = createAdmin(department);
        Nurse nurse1 = createNurse("오로라", "nurse1", department, true);
        Nurse nurse2 = createNurse("최로라", "nurse2", department, true);
        Nurse nurse3 = createNurse("정로라", "nurse3", department, true);
        Nurse nurse4 = createNurse("홍로라", "nurse4", department, false);

        nurseEsRepository.saveAll(
                NurseDocumentConverter.toNurseDocumentList(
                        List.of(nurse1, nurse2, nurse3, nurse4)));

        String nurseName = "로라";
        Long lastIdx = 1L;
        int size = 2;

        // when
        Slice<NurseDocument> response =
                customNurseEsRepository.findActiveNursesByNameAndAdminOrAll(
                        nurseName, admin, lastIdx, size);

        // then
        assertThat(response).hasSize(2);
        assertThat(response.hasNext()).isFalse();
        assertThat(response)
                .extracting("username")
                .contains(nurse2.getUsername(), nurse3.getUsername());
    }

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
