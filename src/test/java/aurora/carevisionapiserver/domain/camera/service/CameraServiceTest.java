package aurora.carevisionapiserver.domain.camera.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoResponse;
import aurora.carevisionapiserver.domain.camera.repository.CameraRepository;
import aurora.carevisionapiserver.domain.camera.repository.CustomVideoRepositoryImpl;
import aurora.carevisionapiserver.domain.camera.repository.VideoRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.PatientService;

class CameraServiceTest extends IntegrationTestSupport {
    @Autowired PatientRepository patientRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired NurseRepository nurseRepository;
    @MockBean CustomVideoRepositoryImpl customVideoRepository;
    @Autowired VideoRepository videoRepository;
    @Autowired CameraRepository cameraRepository;
    @Autowired BedRepository bedRepository;
    @Autowired AdminRepository adminRepository;
    @Autowired CameraService cameraService;
    @MockBean PatientService patientService;

    @AfterEach
    void tearDown() {
        bedRepository.deleteAllInBatch();
        videoRepository.deleteAllInBatch();
        patientRepository.deleteAllInBatch();
        adminRepository.deleteAllInBatch();
        nurseRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
        cameraRepository.deleteAllInBatch();
    }

    @DisplayName("저장된 비디오의 정보를 조회한다.")
    @Test
    void getSavedVideoInfos() {
        // given
        Long lastIdx = -1L;
        int size = 2;

        Department department = createDepartment();
        Nurse nurse = createNurse(department);
        Patient patient = createPatient(1L, department, nurse);
        Video video1 =
                createVideo(
                        patient,
                        "https://carevision-bucket.s3.ap-northeast-1.amazonaws.com/video/1/1.mp4");
        Video video2 =
                createVideo(
                        patient,
                        "https://carevision-bucket.s3.ap-northeast-1.amazonaws.com/video/1/2.mp4");
        List<Video> videos = List.of(video1, video2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        // when
        when(patientService.getPatient(anyLong())).thenReturn(patient);
        when(customVideoRepository.findByPatient(any(Patient.class), anyLong(), anyInt()))
                .thenReturn(new SliceImpl<>(videos));
        Slice<VideoInfoResponse> response =
                cameraService.getSavedVideoInfos(patient.getId(), lastIdx, size);

        // then
        assertThat(response.getContent())
                .hasSize(2)
                .extracting("name")
                .contains(
                        video1.getCreatedAt().format(formatter),
                        video2.getCreatedAt().format(formatter));
    }

    @DisplayName("관리자 과의 모든 카메라 정보를 페이지 처리해 조회한다. 다음으로 조회될 카메라는 없다.")
    @Test
    void getAllCameraInfoWhenHasNextIsFalse() {
        // given
        Department department = createDepartment();
        Camera camera0 = createCamera("CAM0");
        createBed(1L, 1L, 1L, department, camera0);

        Camera camera1 = createCamera("CAM1");
        Camera camera2 = createCamera("CAM2");
        Camera camera3 = createCamera("CAM3");
        createBed(1L, 2L, 2L, department, camera1);
        createBed(1L, 2L, 1L, department, camera2);
        createBed(1L, 1L, 3L, department, camera3);
        Admin admin = createAdmin(department);

        // when
        Slice<Camera> response = cameraService.getAllCameraInfo(admin, "CAM0", 3);

        // then
        assertThat(response.getContent())
                .hasSize(3)
                .extracting("id")
                .containsExactly("CAM3", "CAM2", "CAM1");
        assertThat(response.hasNext()).isFalse();
    }

    @DisplayName("관리자 과의 모든 카메라 정보를 페이지 처리해 조회한다. 다음으로 조회될 카메라가 있다.")
    @Test
    void getAllCameraInfoWhenHasNextIsTrue() {
        // given
        Department department = createDepartment();
        Camera camera0 = createCamera("CAM0");
        createBed(1L, 1L, 1L, department, camera0);

        Camera camera1 = createCamera("CAM1");
        Camera camera2 = createCamera("CAM2");
        Camera camera3 = createCamera("CAM3");
        createBed(1L, 2L, 2L, department, camera1);
        createBed(1L, 2L, 1L, department, camera2);
        createBed(1L, 1L, 3L, department, camera3);
        Admin admin = createAdmin(department);

        // when
        Slice<Camera> response = cameraService.getAllCameraInfo(admin, "CAM0", 2);

        // then
        assertThat(response.getContent())
                .hasSize(2)
                .extracting("id")
                .containsExactly("CAM3", "CAM2");
        assertThat(response.hasNext()).isTrue();
    }

    private Camera createCamera(String id) {
        Camera camera = Camera.builder().id(id).build();
        return cameraRepository.save(camera);
    }

    private Admin createAdmin(Department department) {
        Admin admin = Admin.builder().department(department).username("admin").build();
        return adminRepository.save(admin);
    }

    private Video createVideo(Patient patient, String link) {
        Video video = Video.builder().patient(patient).link(link).build();
        return videoRepository.save(video);
    }

    private Department createDepartment() {
        Hospital hospital = createHospital();
        Department department = Department.builder().hospital(hospital).build();
        return departmentRepository.save(department);
    }

    private Hospital createHospital() {
        Hospital hospital = Hospital.builder().name("hospital").build();
        return hospitalRepository.save(hospital);
    }

    private Nurse createNurse(Department department) {
        Nurse nurse = Nurse.builder().department(department).build();
        return nurseRepository.save(nurse);
    }

    private Patient createPatient(Long id, Department department, Nurse nurse) {
        Patient patient = Patient.builder().id(id).nurse(nurse).department(department).build();
        return patientRepository.save(patient);
    }

    private Bed createBed(
            Long inpatientWardNumber,
            Long patientRoomNumber,
            Long bedNumber,
            Department department,
            Camera camera) {
        return bedRepository.save(
                Bed.builder()
                        .inpatientWardNumber(inpatientWardNumber)
                        .patientRoomNumber(patientRoomNumber)
                        .bedNumber(bedNumber)
                        .department(department)
                        .camera(camera)
                        .build());
    }
}
