package aurora.carevisionapiserver.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.api.AdminNurseController;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;

@WebMvcTest(AdminNurseController.class)
public class AdminNurseControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private NurseService nurseService;

    private Nurse createNurse() {
        Hospital hospital = Hospital.builder().id(1L).name("서울병원").department("성형외과").build();

        String dateTime = "2024-10-11 17:57:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Nurse.builder()
                .id(1L)
                .name("김간호사")
                .username("kim1")
                .registeredAt(LocalDateTime.parse(dateTime, formatter))
                .hospital(hospital)
                .build();
    }

    private Nurse createOtherNurse() {
        Hospital hospital = Hospital.builder().id(2L).name("대구병원").department("성형외과").build();

        String dateTime = "2024-09-10 17:57:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Nurse.builder()
                .id(2L)
                .name("최간호사")
                .username("choi2")
                .registeredAt(LocalDateTime.parse(dateTime, formatter))
                .hospital(hospital)
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("간호사 리스트 조회 성공한다.")
    void getNurseListSuccess() throws Exception {
        List<Nurse> nurses = List.of(createNurse(), createOtherNurse());

        given(nurseService.getNurseList()).willReturn(nurses);

        mockMvc.perform(get("/api/admin/nurses").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.nurseList[1].name").value("최간호사"))
                .andExpect(jsonPath("$.result.nurseList[1].id").value("choi2"))
                .andExpect(jsonPath("$.result.nurseList[0].name").value("김간호사"))
                .andExpect(jsonPath("$.result.nurseList[0].id").value("kim1"))
                .andExpect(jsonPath("$.result.count").value(2));
    }

    @Test
    @WithMockUser
    @DisplayName("간호사 검색에 성공한다.")
    void searchNurseSuccess() throws Exception {
        Nurse nurse = createNurse();
        String nurseName = nurse.getName();

        given(nurseService.searchNurse(nurseName)).willReturn(List.of(nurse));

        mockMvc.perform(
                        get("/api/admin/nurses/search")
                                .param("search", nurseName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.nurseList[0].name").value("김간호사"))
                .andExpect(jsonPath("$.result.nurseList[0].id").value("kim1"))
                .andExpect(jsonPath("$.result.count").value(1));
    }
}
