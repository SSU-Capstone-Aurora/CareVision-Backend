package aurora.carevisionapiserver.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.api.NurseController;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;

@WebMvcTest(NurseController.class)
class NurseControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private NurseService nurseService;

    private Nurse createNurse() {
        Hospital hospital = Hospital.builder().id(1L).name("서울병원").department("성형외과").build();

        String dateTime = "2024-10-11 17:57:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Nurse.builder()
                .id(1L)
                .name("김간호사")
                .username("nurse1")
                .registeredAt(LocalDateTime.parse(dateTime, formatter))
                .hospital(hospital)
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("간호사 마이페이지 조회 성공한다.")
    void getNurseMyPageSuccess() throws Exception {
        Nurse nurse = createNurse();
        Long nurseId = nurse.getId();

        given(nurseService.getNurse(nurseId)).willReturn(Optional.of(nurse));
        when(nurseService.existsByNurseId(nurseId)).thenReturn(true);

        mockMvc.perform(
                        get("/api/nurse/profile")
                                .param("nurseId", String.valueOf(nurseId))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.name").value("김간호사"))
                .andExpect(jsonPath("$.result.registeredAt").value("2024-10-11"))
                .andExpect(jsonPath("$.result.hospitalName").value("서울병원"))
                .andExpect(jsonPath("$.result.department").value("성형외과"));
    }
}
