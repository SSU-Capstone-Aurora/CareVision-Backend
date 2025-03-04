package aurora.carevisionapiserver.domain.nurse.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import aurora.carevisionapiserver.ControllerTestSupport;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

class NurseControllerTest extends ControllerTestSupport {

    @WithMockUser
    @DisplayName("간호사와 연결되지 않은 환자 목록을 검색합니다.")
    @Test
    void searchUnlinkedPatientList() throws Exception {
        // given
        PatientSearchListResponse patientSearchListResponse =
                PatientSearchListResponse.builder().patientList(List.of()).build();

        // when
        when(patientService.searchUnlinkedPatients(any())).thenReturn(patientSearchListResponse);

        // then
        mockMvc.perform(
                        get("/api/patients/unlinked/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .param("search", "환자명"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.patientList").isArray())
                .andExpect(jsonPath("$.result.count").isNumber());
    }
}
