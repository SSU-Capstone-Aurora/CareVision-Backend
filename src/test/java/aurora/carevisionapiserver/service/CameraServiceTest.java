package aurora.carevisionapiserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.repository.CameraRepository;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.camera.service.Impl.CameraServiceImpl;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

@ExtendWith(MockitoExtension.class)
class CameraServiceTest {
    @Mock CameraRepository cameraRepository;
    CameraService cameraService;

    private static final String CAMERA_ID_1 = "01-numberCode";
    private static final String CAMERA_ID_2 = "02-numberCode";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cameraService = new CameraServiceImpl(cameraRepository);
    }

    private Camera createCamera() {
        Bed bed1 =
                Bed.builder()
                        .id(1L)
                        .inpatientWardNumber(1L)
                        .patientRoomNumber(2L)
                        .bedNumber(2L)
                        .build();
        Patient patient1 = Patient.builder().id(1L).name("Ye Rim").bed(bed1).build();

        return Camera.builder().id(CAMERA_ID_1).patient(patient1).build();
    }

    private Camera createAnotherCamera() {
        Bed bed2 =
                Bed.builder()
                        .id(2L)
                        .inpatientWardNumber(1L)
                        .patientRoomNumber(2L)
                        .bedNumber(1L)
                        .build();
        Patient patient2 = Patient.builder().id(2L).name("Suk Hee").bed(bed2).build();
        return Camera.builder().id(CAMERA_ID_2).patient(patient2).build();
    }

    @Test
    @WithMockUser
    @DisplayName("카메라를 정렬해 조회한다.")
    void getCameraBySort() {
        Camera camera1 = createCamera();
        Camera camera2 = createAnotherCamera();

        when(cameraRepository.findAllCamerasSortedByBed(1))
                .thenReturn(Arrays.asList(camera2, camera1));

        List<Camera> cameras = cameraService.getCameras();

        assertEquals(2, cameras.size());
        assertEquals(CAMERA_ID_1, cameras.get(1).getId());
        assertEquals(CAMERA_ID_2, cameras.get(0).getId());
    }
}
