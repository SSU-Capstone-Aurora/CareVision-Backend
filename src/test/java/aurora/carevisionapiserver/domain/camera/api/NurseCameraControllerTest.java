package aurora.carevisionapiserver.domain.camera.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import aurora.carevisionapiserver.ControllerTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoResponse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

class NurseCameraControllerTest extends ControllerTestSupport {
    @WithMockUser
    @DisplayName("담당하는 환자의 전체 스트리밍 관련 정보를 조회한다.")
    @Test
    void getStreamingInfoList() throws Exception {
        // given
        Bed bed = Bed.builder().bedNumber(1L).patientRoomNumber(2L).inpatientWardNumber(3L).build();
        Patient patient = Patient.builder().id(1L).bed(bed).build();
        Slice<Patient> patientSlice = new SliceImpl<>(List.of(patient));

        // when //then
        when(patientService.getPatientSlice(any(), anyLong(), anyInt())).thenReturn(patientSlice);
        when(cameraService.getStreamingInfo(any()))
                .thenReturn(Collections.singletonMap(patientSlice.getContent().get(0), "info"));
        when(pageService.getNextCursor(any())).thenReturn(1L);

        mockMvc.perform(
                        get("/api/streaming")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .param("lastIdx", "0")
                                .param("size", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.streamingResponse").isArray())
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.nextCursor").value(1L));
    }

    @WithMockUser
    @DisplayName("환자의 비디오 정보를 조회한다.")
    @Test
    void getSavedVideoInfos() throws Exception {
        // given
        VideoInfoResponse response = VideoInfoResponse.builder().build();
        Slice<VideoInfoResponse> videoInfo = new SliceImpl<>(List.of(response));

        // when //then
        when(cameraService.getSavedVideoInfos(anyLong(), anyLong(), anyInt()))
                .thenReturn(videoInfo);
        when(pageService.getNextCursor(any())).thenReturn(1L);

        mockMvc.perform(
                        get("/api/patients/{patientId}/videos", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .param("lastIdx", "0")
                                .param("size", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.videoInfoList").isArray())
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.nextCursor").value(1L));
    }
}
