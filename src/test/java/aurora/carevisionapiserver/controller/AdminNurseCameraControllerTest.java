package aurora.carevisionapiserver.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.camera.api.AdminCameraController;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

@WebMvcTest(AdminCameraController.class)
class AdminNurseCameraControllerTest {

    @MockBean private CameraService cameraService;
    @Autowired private MockMvc mockMvc;

    private static final String CAMERA_ID_1 = "01-numberCode";
    private static final String CAMERA_ID_2 = "02-numberCode";

    private Camera createCamera() {
        Bed bed1 =
                Bed.builder()
                        .id(1L)
                        .inpatientWardNumber(1L)
                        .patientRoomNumber(3L)
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
                        .bedNumber(3L)
                        .build();
        Patient patient2 = Patient.builder().id(2L).name("Suk Hee").bed(bed2).build();
        return Camera.builder().id(CAMERA_ID_2).patient(patient2).build();
    }

    @Test
    @WithMockUser
    @DisplayName("카메라 목록을 조회한다.")
    void getCameras() throws Exception {
        List<Camera> cameras = Arrays.asList(createCamera(), createAnotherCamera());

        given(cameraService.getCameras()).willReturn(cameras);

        mockMvc.perform(get("/api/admin/cameras").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cameraInfoList").exists())
                .andExpect(jsonPath("$.result.cameraInfoList.length()").value(2))
                .andExpect(jsonPath("$.result.totalCount").value(2));
    }
}
