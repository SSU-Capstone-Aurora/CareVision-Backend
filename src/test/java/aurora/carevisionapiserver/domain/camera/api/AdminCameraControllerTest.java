package aurora.carevisionapiserver.domain.camera.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import aurora.carevisionapiserver.ControllerTestSupport;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.global.common.dto.request.PageForCameraRequest;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

class AdminCameraControllerTest extends ControllerTestSupport {
    @WithMockUser
    @DisplayName("카메라 목록을 조회합니다.")
    @Test
    void getCameras() throws Exception {
        // given
        PageForCameraRequest request = new PageForCameraRequest("CAM1", 2);
        Slice<Camera> cameras = new SliceImpl<>(List.of());

        // when //then
        when(cameraService.getAllCameraInfo(any(), any())).thenReturn(cameras);
        when(pageService.getNextCursorForCameras(any(), any())).thenReturn("CAM2");

        mockMvc.perform(
                        get("/api/admin/cameras")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.cameraInfoList").isArray())
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.nextCursor").value("CAM2"));
    }
}
