package aurora.carevisionapiserver.domain.nurse.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;

class CustomNurseRepositoryImplTest extends IntegrationTestSupport {
    @Autowired PatientRepository patientRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired BedRepository bedRepository;
    @Autowired CustomNurseRepositoryImpl customNurseRepository;

    @AfterEach
    void tearDown() {
        nurseRepository.deleteAllInBatch();
        bedRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
    }

    @DisplayName("관리자의 부서에 근무하는 활성화된 간호사를 조회한다.")
    @Test
    void findActiveNursesByAdmin() {
        // given
        Department department = createDepartment();
        Admin admin = createAdmin(department);
        Nurse nurse1 = createNurse("nurse1", department, true);
        Nurse nurse2 = createNurse("nurse2", department, true);
        createNurse("nurse3", department, true);
        createNurse("nurse4", department, false);

        Long lastIdx = -1L;
        int size = 2;

        // when
        Slice<Nurse> response = customNurseRepository.findActiveNursesByAdmin(admin, lastIdx, size);

        // then
        assertThat(response).hasSize(size);
        assertThat(response.hasNext()).isTrue();
        assertThat(response)
                .extracting("username")
                .contains(nurse1.getUsername(), nurse2.getUsername());
    }

    private static Admin createAdmin(Department department) {
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

    private Nurse createNurse(String username, Department department, boolean isActivated) {
        Nurse nurse =
                Nurse.builder()
                        .department(department)
                        .username(username)
                        .isActivated(isActivated)
                        .build();
        return nurseRepository.save(nurse);
    }
}
