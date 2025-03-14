package aurora.carevisionapiserver.domain.bed.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;

@Transactional
class CustomBedRepositoryTest extends IntegrationTestSupport {
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired BedRepository bedRepository;

    @DisplayName("다음으로 조회될 카메라의 bed를 조회한다.")
    @Test
    void findNextBeds() {
        // given
        int size = 3;
        Hospital hospital = createHospital();
        Department department = createDepartment(hospital);
        Bed lastBed = createBed(1L, 1L, 1L, 1L, department);
        createBed(2L, 1L, 2L, 1L, department);
        createBed(3L, 1L, 3L, 2L, department);
        createBed(4L, 1L, 4L, 3L, department);

        // when
        List<Bed> beds = bedRepository.findNextBeds(department, lastBed, size);

        // then
        assertThat(beds).hasSize(3).extracting("id").containsExactly(2L, 3L, 4L);
    }

    private Department createDepartment(Hospital hospital) {
        Department department = Department.builder().name("department").hospital(hospital).build();
        return departmentRepository.save(department);
    }

    private Bed createBed(
            Long id,
            Long inpatientWardNumber,
            Long patientRoomNumber,
            Long bedNumber,
            Department department) {
        return bedRepository.save(
                Bed.builder()
                        .id(id)
                        .inpatientWardNumber(inpatientWardNumber)
                        .patientRoomNumber(patientRoomNumber)
                        .bedNumber(bedNumber)
                        .department(department)
                        .build());
    }

    private Hospital createHospital() {
        Hospital hospital = Hospital.builder().name("hospital").build();
        return hospitalRepository.save(hospital);
    }
}
