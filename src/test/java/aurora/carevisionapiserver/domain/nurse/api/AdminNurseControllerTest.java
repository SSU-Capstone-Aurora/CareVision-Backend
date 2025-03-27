package aurora.carevisionapiserver.domain.nurse.api;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import aurora.carevisionapiserver.ControllerTestSupport;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewPageResponse;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

class AdminNurseControllerTest extends ControllerTestSupport {

    @WithMockUser(
            username = "admin",
            roles = {"ADMIN"})
    @DisplayName("간호사의 이름 검색에 성공한다.")
    @Test
    void searchNurseList() throws Exception {
        // given
        NursePreviewPageResponse response =
                NursePreviewPageResponse.builder()
                        .nurseList(List.of())
                        .nextCursor(-1L)
                        .hasNext(false)
                        .build();

        // when
        when(nurseService.searchActiveNurses(any(), anyLong(), anyInt(), any()))
                .thenReturn(response);

        // then
        mockMvc.perform(
                        get("/api/admin/nurses/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .param("lastIdx", "0")
                                .param("size", "2")
                                .param("search", "간호사명"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.nurseList").isArray())
                .andExpect(jsonPath("$.result.hasNext").isBoolean())
                .andExpect(jsonPath("$.result.nextCursor").isNumber());
    }
}
