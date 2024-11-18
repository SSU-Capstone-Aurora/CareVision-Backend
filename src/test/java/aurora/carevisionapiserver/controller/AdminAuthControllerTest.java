package aurora.carevisionapiserver.controller;

import static org.mockito.ArgumentMatchers.any;
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

import aurora.carevisionapiserver.domain.admin.api.AdminAuthController;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.util.AdminUtils;
import aurora.carevisionapiserver.util.HospitalUtils;

@WebMvcTest(AdminAuthController.class)
public class AdminAuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private AdminService adminService;
    @MockBean private HospitalService hospitalService;
    @MockBean private AuthService authService;

    private static final String ADMIN_SIGN_UP_REQUEST_JSON =
            """
            {
                "admin": {
                    "username": "admin1",
                    "password": "password123",
                    "department": "성형외과"
                },
                "hospital": {
                    "name": "오로라 병원",
                    "department": "성형외과"
                }
            }
        """;

    @Test
    @WithMockUser
    @DisplayName("회원가입에 성공한다.")
    public void testCreateAdminSuceess() throws Exception {
        // Given
        Hospital hospital = HospitalUtils.createHospital();
        Admin admin = AdminUtils.createAdmin(hospital);

        // When
        given(hospitalService.createHospital(any(HospitalCreateRequest.class)))
                .willReturn(hospital);
        given(
                        adminService.createAdmin(
                                any(AdminCreateRequest.class),
                                any(Hospital.class),
                                any(Department.class)))
                .willReturn(admin);

        // Then
        mockMvc.perform(
                        post("/api/admin/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ADMIN_SIGN_UP_REQUEST_JSON)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.admin.id").value(1))
                .andExpect(jsonPath("$.result.hospital.name").value("오로라 병원"))
                .andExpect(jsonPath("$.result.hospital.department").value("성형외과"));
    }

    @Test
    @WithMockUser
    @DisplayName("관리자 회원가입 중복 체크에 성공한다.")
    public void testCheckUsernameSuccess() throws Exception {
        // Given
        String username = "admin1";

        // When
        when(authService.isUsernameDuplicated(username)).thenReturn(false);

        // Then
        mockMvc.perform(
                        get("/api/admin/check-username")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("username", username)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus.USERNAME_AVAILABLE.getCode()))
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("관리자 회원가입 중복 체크에 실패한다.")
    public void testCheckUsernameFailure() throws Exception {
        // Given
        String username = "admin1";

        // When
        when(authService.isUsernameDuplicated(username)).thenReturn(true);

        // Then
        mockMvc.perform(
                        get("/api/admin/check-username")
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
