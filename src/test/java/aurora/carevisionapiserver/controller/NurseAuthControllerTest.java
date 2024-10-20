package aurora.carevisionapiserver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.domain.nurse.api.NurseAuthController;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;

@WebMvcTest(NurseAuthController.class)
public class NurseAuthControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockBean private NurseService nurseService;

    @MockBean private HospitalService hospitalService;

    private static final String NURSE_SIGN_UP_REQUEST_JSON =
            """
            {
                "nurse": {
                    "username": "nurse1",
                    "password": "password123",
                    "name": "오로라"
                },
                "hospital": {
                    "id": 1
                }
            }
            """;

    private Hospital createHospital() {
        return Hospital.builder().id(1L).name("오로라 병원").department("정형외과").build();
    }

    private Nurse createNurse(Hospital hospital) {
        return Nurse.builder().id(1L).username("nurse1").hospital(hospital).name("오로라").build();
    }

    @Test
    @WithMockUser
    @DisplayName("간호사 회원가입에 성공한다.")
    public void testCreateNurseSuccess() throws Exception {
        // Given
        Hospital hospital = createHospital();
        Nurse nurse = createNurse(hospital);

        // When
        given(hospitalService.getHospital(anyLong())).willReturn(hospital);
        given(nurseService.createNurse(any(NurseCreateRequest.class), any(Hospital.class)))
                .willReturn(nurse);

        // Then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(NURSE_SIGN_UP_REQUEST_JSON)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.message").value("성공입니다."))
                .andExpect(jsonPath("$.result.nurse.id").value(1))
                .andExpect(jsonPath("$.result.hospital.name").value("오로라 병원"))
                .andExpect(jsonPath("$.result.hospital.department").value("정형외과"));
    }

    @Test
    @WithMockUser
    @DisplayName("병원을 찾을 수 없어 실패한다.")
    public void testCreateNurseHospital_NotFound() throws Exception {
        // Given
        given(hospitalService.getHospital(anyLong()))
                .willThrow(new HospitalException(ErrorStatus.HOSPITAL_NOT_FOUND));

        // When & Then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(NURSE_SIGN_UP_REQUEST_JSON)
                                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("HOSPITAL400"))
                .andExpect(jsonPath("$.message").value("병원을 찾을 수 없습니다."));
    }
}
