package aurora.carevisionapiserver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;

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
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.name").value("오로라"));
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

    @Test
    @WithMockUser
    @DisplayName("간호사 회원가입 중복 체크에 성공한다.")
    public void testCheckUsernameSuccess() throws Exception {
        // Given
        String username = "nurse1";

        // When
        when(nurseService.isUsernameDuplicated(username)).thenReturn(false);

        // Then
        mockMvc.perform(
                        get("/api/check-username")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("username", username)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._USERNAME_AVAILABLE.getCode()))
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("간호사 회원가입 중복 체크에 실패한다.")
    public void testCheckUsernameFailure() throws Exception {
        // Given
        String username = "nurse1";

        // When
        when(nurseService.isUsernameDuplicated(username)).thenReturn(true);

        // Then
        mockMvc.perform(
                        get("/api/check-username")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("username", username)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorStatus.USERNAME_DUPLICATED.getCode()))
                .andExpect(
                        jsonPath("$.message").value(ErrorStatus.USERNAME_DUPLICATED.getMessage()))
                .andExpect(jsonPath("$.result").value(false));
    }
}
