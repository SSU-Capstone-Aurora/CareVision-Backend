package aurora.carevisionapiserver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.domain.nurse.api.NurseAuthController;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.util.HospitalUtils;
import aurora.carevisionapiserver.util.NurseUtils;

@WebMvcTest(NurseAuthController.class)
public class NurseAuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private NurseService nurseService;
    @MockBean private HospitalService hospitalService;
    @MockBean private AuthService authService;
    @MockBean private AdminService adminService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private NurseRepository nurseRepository;

    private static final String ACTIVE_NURSE_ONLY_URL = "/api/patients";

    @Test
    @WithMockUser
    @DisplayName("간호사 회원가입에 성공한다.")
    public void testCreateNurseSuccess() throws Exception {
        Hospital hospital = HospitalUtils.createHospital();
        Nurse nurse = NurseUtils.createInActiveNurse();

        given(hospitalService.getHospital(anyLong())).willReturn(hospital);
        given(nurseService.createNurse(any(NurseCreateRequest.class), any(Hospital.class)))
                .willReturn(nurse);

        Map<String, Object> nurseSignUpRequest = new HashMap<>();
        Map<String, Object> nurseDetails = new HashMap<>();
        nurseDetails.put("username", "nurse1");
        nurseDetails.put("password", "password123");
        nurseDetails.put("name", "오로라");
        nurseSignUpRequest.put("nurse", nurseDetails);

        Map<String, Object> hospitalDetails = new HashMap<>();
        hospitalDetails.put("id", 1);
        nurseSignUpRequest.put("hospital", hospitalDetails);

        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nurseSignUpRequest))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessStatus._OK.getMessage()))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.name").value(nurse.getName()));
    }

    @Test
    @WithMockUser
    @DisplayName("병원을 찾을 수 없어 실패한다.")
    public void testCreateNurseHospital_NotFound() throws Exception {

        Map<String, Object> nurseSignUpRequest = new HashMap<>();
        Map<String, Object> nurseDetails = new HashMap<>();
        nurseDetails.put("username", "nurse1");
        nurseDetails.put("password", "password123");
        nurseDetails.put("name", "오로라");
        nurseSignUpRequest.put("nurse", nurseDetails);

        Map<String, Object> hospitalDetails = new HashMap<>();
        hospitalDetails.put("id", 1);
        nurseSignUpRequest.put("hospital", hospitalDetails);

        given(hospitalService.getHospital(anyLong()))
                .willThrow(new HospitalException(ErrorStatus.HOSPITAL_NOT_FOUND));

        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nurseSignUpRequest))
                                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value(ErrorStatus.HOSPITAL_NOT_FOUND.getCode()))
                .andExpect(
                        jsonPath("$.message").value(ErrorStatus.HOSPITAL_NOT_FOUND.getMessage()));
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
                .andExpect(jsonPath("$.code").value(SuccessStatus.USERNAME_AVAILABLE.getCode()))
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

    @Test
    @DisplayName("활성화된 Nurse는 성공적으로 로그인하여 accessToken과 refreshToken을 받는다.")
    @WithMockUser
    void testSuccessfulLoginWithActiveNurse() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "kim1");
        loginRequest.put("password", "password123");

        Nurse activeNurse = NurseUtils.createActiveNurse();
        given(nurseRepository.findByUsername("kim1")).willReturn(Optional.of(activeNurse));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("kim1", "password123");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authToken);

        given(authService.createAccessToken("kim1", "NURSE")).willReturn("testAccessToken");
        given(authService.createRefreshToken("kim1", "NURSE")).willReturn("testRefreshToken");

        Cookie refreshTokenCookie = new Cookie("refreshToken", "testRefreshToken");
        given(authService.createRefreshTokenCookie("testRefreshToken"))
                .willReturn(refreshTokenCookie);

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("testAccessToken"))
                .andExpect(cookie().value("refreshToken", "testRefreshToken"));
    }

    @Test
    @DisplayName("비활성화된 Nurse는 로그인에 실패한다.")
    void testFailedLoginWithInactiveNurse() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "kim2");
        loginRequest.put("password", "password123");

        Nurse inactiveNurse = NurseUtils.createInActiveNurse();
        given(nurseRepository.findByUsername("kim2")).willReturn(Optional.of(inactiveNurse));

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorStatus.USER_NOT_ACTIVATED.getCode()))
                .andExpect(
                        jsonPath("$.message").value(ErrorStatus.USER_NOT_ACTIVATED.getMessage()));
    }
}
