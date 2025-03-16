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
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import aurora.carevisionapiserver.ControllerTestSupport;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewPageResponse;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

class AdminNurseControllerTest extends ControllerTestSupport {

    @WithMockUser(
            username = "admin",
            roles = {"ADMIN"})
    @DisplayName("간호사의 이름 검색에 성공한다.")
    @Test
    void searchNurseList() throws Exception {
        // given
        PageRequest request = new PageRequest(-1L, 2);
        NursePreviewPageResponse response =
                NursePreviewPageResponse.builder()
                        .nurseList(List.of())
                        .nextCursor(-1L)
                        .hasNext(false)
                        .build();

        // when
        when(nurseService.searchActiveNurses(any(), any(), any())).thenReturn(response);

        // then
        mockMvc.perform(
                        get("/api/admin/nurses/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .param("search", "간호사명"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.nurseList").isArray())
                .andExpect(jsonPath("$.result.hasNext").isBoolean())
                .andExpect(jsonPath("$.result.nextCursor").isNumber());
    }
}
