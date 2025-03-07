package aurora.carevisionapiserver.domain.camera.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;

@Transactional
class CustomCameraRepositoryImplTest extends IntegrationTestSupport {
    @Autowired CustomCameraRepositoryImpl customCameraRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired BedRepository bedRepository;
    @Autowired CameraRepository cameraRepository;

    @DisplayName("해당 병원의 모든 카메라 정보를 처음 조회한다.")
    @Test
    void findAllCamerasSortedByBedFirst() {
        // given
        Long lastIdx = -1L;
        int size = 2;

        Hospital hospital = createHospital();
        Department department = createDepartment(hospital);
        Bed bed1 = createBed(1L, 1L, 1L, department);
        Bed bed2 = createBed(2L, 2L, 2L, department);
        Bed bed3 = createBed(3L, 3L, 3L, department);
        createCamera("CAM1", bed1);
        createCamera("CAM2", bed2);
        createCamera("CAM3", bed3);

        // when
        Slice<Camera> response =
                customCameraRepository.findAllCamerasSortedByBed(department, lastIdx, size);

        // then
        assertThat(response.getContent()).hasSize(2).extracting("id").contains("CAM1", "CAM2");
        assertThat(response.hasNext()).isTrue();
    }

    @DisplayName("해당 병원의 모든 카메라 정보를 조회한다. 요청된 마지막 커서 값이 1L이며 조회되는 개수는 2개이다.")
    @Test
    void findAllCamerasSortedByBed() {
        // given
        Long lastIdx = 1L;
        int size = 2;

        Hospital hospital = createHospital();
        Department department = createDepartment(hospital);
        Bed bed1 = createBed(1L, 1L, 1L, department);
        Bed bed2 = createBed(2L, 2L, 2L, department);
        Bed bed3 = createBed(3L, 3L, 3L, department);
        createCamera("CAM1", bed1);
        createCamera("CAM2", bed2);
        createCamera("CAM3", bed3);

        // when
        Slice<Camera> response =
                customCameraRepository.findAllCamerasSortedByBed(department, lastIdx, size);

        // then
        assertThat(response.getContent()).hasSize(2).extracting("id").contains("CAM2", "CAM3");
        assertThat(response.hasNext()).isFalse();
    }

    private Camera createCamera(String id, Bed bed) {
        Camera camera = Camera.builder().id(id).bed(bed).build();
        return cameraRepository.save(camera);
    }

    private Department createDepartment(Hospital hospital) {
        Department department = Department.builder().name("department").hospital(hospital).build();
        return departmentRepository.save(department);
    }

    private Bed createBed(
            Long inpatientWardNumber,
            Long patientRoomNumber,
            Long bedNumber,
            Department department) {
        return bedRepository.save(
                Bed.builder()
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
