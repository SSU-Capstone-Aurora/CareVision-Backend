package aurora.carevisionapiserver.controller;

import static org.mockito.ArgumentMatchers.any;
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

import aurora.carevisionapiserver.domain.admin.api.AdminAuthController;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;

@WebMvcTest(AdminAuthController.class)
public class AdminAuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private AdminService adminService;
    @MockBean private HospitalService hospitalService;

    private static final String ADMIN_SIGN_UP_REQUEST_JSON =
            """
            {
                "adminCreateRequest": {
                    "username": "admin1",
                    "password": "password123",
                    "department": "성형외과"
                },
                "hospitalCreateRequest": {
                    "name": "오로라 병원",
                    "department": "성형외과"
                }
            }
        """;

    private Hospital createHospital() {
        return Hospital.builder().id(1L).name("오로라 병원").department("성형외과").build();
    }

    private Admin createAdmin(Hospital hospital) {
        return Admin.builder().id(1L).username("admin1").hospital(hospital).build();
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입에 성공한다.")
    public void testCreateAdminSuceess() throws Exception {
        // Given
        Hospital hospital = createHospital();
        Admin admin = createAdmin(hospital);

        // When
        given(hospitalService.createHospital(any(HospitalCreateRequest.class)))
                .willReturn(hospital);
        given(adminService.createAdmin(any(AdminCreateRequest.class), any(Hospital.class)))
                .willReturn(admin);

        // Then
        mockMvc.perform(
                        post("/api/admin/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ADMIN_SIGN_UP_REQUEST_JSON)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.hospital").value("오로라 병원"))
                .andExpect(jsonPath("$.result.department").value("성형외과"));
    }
}
