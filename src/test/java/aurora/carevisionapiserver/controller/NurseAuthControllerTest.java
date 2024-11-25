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

import java.util.Collections;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.util.validation.validator.IsActivateNurseValidator;
import aurora.carevisionapiserver.util.HospitalUtils;
import aurora.carevisionapiserver.util.NurseUtils;

@WebMvcTest(NurseAuthController.class)
public class NurseAuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private NurseService nurseService;
    @MockBean private HospitalService hospitalService;
    @MockBean private AuthService authService;
    @MockBean private NurseRepository nurseRepository;
    @MockBean private JWTUtil jwtUtil;
    @MockBean private AdminService adminService;
    @MockBean private IsActivateNurseValidator isActivateNurseValidator;

    @Test
    @WithMockUser
    @DisplayName("간호사 회원가입에 성공한다.")
    public void testCreateNurseSuccess() throws Exception {
        Hospital hospital = HospitalUtils.createHospital();
        Nurse nurse = NurseUtils.createInactiveNurse();

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
        when(authService.isUsernameDuplicated(username)).thenReturn(false);

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
        when(authService.isUsernameDuplicated(username)).thenReturn(true);

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
        Nurse activeNurse = NurseUtils.createActiveNurse();

        String username = activeNurse.getUsername();
        String password = activeNurse.getPassword();
        String role = activeNurse.getRole().toString();
        String refreshToken = "testRefreshToken";
        String accessToken = "testAccessToken";

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        when(nurseRepository.findByUsername(username)).thenReturn(Optional.of(activeNurse));

        when(authService.createAccessToken(username)).thenReturn(accessToken);
        when(authService.createRefreshToken(username)).thenReturn(refreshToken);
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        when(authService.createRefreshTokenCookie(refreshToken)).thenReturn(refreshTokenCookie);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

        when(authService.authenticate(username, password)).thenReturn(Optional.of(authentication));

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").value(accessToken))
                .andExpect(cookie().value("refreshToken", refreshToken));
    }

    @Test
    @DisplayName("비활성화된 Nurse는 로그인에 실패한다.")
    @WithMockUser
    void testFailedLoginWithInactiveNurse() throws Exception {

        Nurse inactiveNurse = NurseUtils.createInactiveNurse();

        String username = inactiveNurse.getUsername();
        String password = inactiveNurse.getPassword();

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        given(nurseRepository.findByUsername(username)).willReturn(Optional.of(inactiveNurse));

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorStatus.USER_NOT_ACTIVATED.getCode()))
                .andExpect(
                        jsonPath("$.message").value(ErrorStatus.USER_NOT_ACTIVATED.getMessage()));
    }

    @Test
    @DisplayName("존재하지 않는 간호사로 로그인하면 인증에 실패한다.")
    @WithMockUser
    void testFailedLoginWithNonExistentNurse() throws Exception {
        String username = "unknownNurse";
        String password = "password123";

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        given(nurseRepository.findByUsername(username)).willReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorStatus.INVALID_CREDENTIALS.getCode()))
                .andExpect(
                        jsonPath("$.message").value(ErrorStatus.INVALID_CREDENTIALS.getMessage()));
    }
}
