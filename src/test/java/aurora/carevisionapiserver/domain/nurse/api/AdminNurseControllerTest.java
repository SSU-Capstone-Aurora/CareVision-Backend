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
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

class AdminNurseControllerTest extends ControllerTestSupport {
    @WithMockUser
    @DisplayName("활성화된 담당 간호사 조회에 성공한다.")
    @Test
    void getNurseList() throws Exception {
        // given
        PageRequest request = new PageRequest(-1L, 2);

        Nurse nurse = Nurse.builder().build();
        Slice<Nurse> nurses = new SliceImpl<>(List.of(nurse));

        // when
        when(nurseService.getActiveNurses(any(), any())).thenReturn(nurses);
        when(pageService.getNextCursor(any())).thenReturn(1L);

        // then
        mockMvc.perform(
                        get("/api/admin/nurses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.nurseList").isArray())
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.nextCursor").value(1L));
    }
}
