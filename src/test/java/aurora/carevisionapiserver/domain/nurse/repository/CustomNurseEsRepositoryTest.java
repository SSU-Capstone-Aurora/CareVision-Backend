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
    }

    @DisplayName("관리자의 부서에 근무하는 활성화된 간호사를 간호사 이름으로 검색한다. (처음 조회)")
    @Test
    void searchNurseByNameAndAdmin() {
        // given
        Department department = createDepartment();
        Admin admin = createAdmin(department);
        Nurse nurse1 = createNurse("nurse1", "nurse1", department, true);
        Nurse nurse2 = createNurse("nurse2", "nurse2", department, false);

        nurseEsRepository.saveAll(
                NurseDocumentConverter.toNurseDocumentList(List.of(nurse1, nurse2)));

        String nurseName = "nurse";
        Long lastIdx = -1L;
        int size = 2;

        // when
        Slice<NurseDocument> response =
                customNurseEsRepository.findActiveNursesByNameAndAdmin(
                        nurseName, admin, lastIdx, size);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.hasNext()).isFalse();
        assertThat(response).extracting("username").contains(nurse1.getUsername());
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
