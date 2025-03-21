package aurora.carevisionapiserver.domain.patient.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

class AdminPatientControllerTest extends ControllerTestSupport {
    @WithMockUser
    @DisplayName("환자 리스트를 조회한다.")
    @Test
    void getPatients() throws Exception {
        // given
        Slice<Patient> patientSlice = new SliceImpl<>(List.of());

        // when //then
        when(patientService.getPatients(any(), anyLong(), anyInt())).thenReturn(patientSlice);
        when(pageService.getNextCursor(any())).thenReturn(1L);

        mockMvc.perform(
                        get("/api/admin/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .param("lastIdx", "0")
                                .param("size", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.patientList").isArray())
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.nextCursor").value(1L));
    }
}
