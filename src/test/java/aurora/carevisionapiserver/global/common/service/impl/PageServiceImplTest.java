package aurora.carevisionapiserver.global.common.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.global.common.domain.Identifiable;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;
import aurora.carevisionapiserver.global.common.service.PageService;

class PageServiceImplTest extends IntegrationTestSupport {
    @Autowired PatientRepository patientRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired BedRepository bedRepository;
    @Autowired PageService pageService;

    @AfterEach
    void tearDown() {
        nurseRepository.deleteAllInBatch();
        bedRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
    }

    @DisplayName("주어진 리스트에서 마지막 엔티티의 ID를 반환한다.")
    @Test
    void getNextCursor() {
        // given
        PageRequest request = new PageRequest(-1L, 3);
        List<Identifiable> entities = new ArrayList<>();
        Department department = createDepartment();
        entities.add(createNurse(1L, "nurse1", department));
        entities.add(createNurse(2L, "nurse2", department));
        entities.add(createNurse(3L, "nurse3", department));

        // when
        Long nextCursor = pageService.getNextCursor(request, entities);

        // then
        assertThat(nextCursor).isEqualTo(3L);
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

    private Nurse createNurse(Long id, String username, Department department) {
        Nurse nurse =
                Nurse.builder()
                        .id(id)
                        .department(department)
                        .username(username)
                        .isActivated(true)
                        .build();
        return nurseRepository.save(nurse);
    }
}
