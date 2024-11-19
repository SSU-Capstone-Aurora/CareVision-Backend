package aurora.carevisionapiserver.controller;

import static aurora.carevisionapiserver.util.PatientUtil.createPatient;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.api.AdminPatientController;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientRegisterRequest;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import aurora.carevisionapiserver.util.CameraUtils;
import aurora.carevisionapiserver.util.PatientUtil;

@WebMvcTest(AdminPatientController.class)
class AdminPatientControllerTest {

    @MockBean private PatientService patientService;
    @MockBean private CameraService cameraService;
    @MockBean private AdminService adminService;
    @MockBean private NurseService nurseService;
    @MockBean private JWTUtil jwtUtil;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("환자를 검색에 성공한다.")
    void searchPatientSuccess() throws Exception {
        String patientName = "test";
        List<Patient> patients = List.of(createPatient(), PatientUtil.createOtherPatient());
        given(patientService.searchPatient(patientName)).willReturn(patients);

        mockMvc.perform(
                        get("/api/admin/patients/search")
                                .param("search", patientName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.patientList").exists())
                .andExpect(jsonPath("$.result.patientList.length()").value(2))
                .andExpect(jsonPath("$.result.patientList[0].patientName").value("test"))
                .andExpect(jsonPath("$.result.patientList[0].inpatientWardNumber").value(1))
                .andExpect(jsonPath("$.result.patientList[0].code").value("kk-123"))
                .andExpect(jsonPath("$.result.patientList[1].patientName").value("kangrok"))
                .andExpect(jsonPath("$.result.patientList[1].inpatientWardNumber").value(2))
                .andExpect(jsonPath("$.result.patientList[1].code").value("rr-123"));
    }

    @Test
    @WithMockUser
    @DisplayName("환자 등록에 성공한다.")
    void createPatientSuccess() throws Exception {
        PatientCreateRequest patientCreateRequest = PatientUtil.createPatientCreateRequest();
        CameraSelectRequest cameraSelectRequest = CameraUtils.createCameraSelectRequest();
        PatientRegisterRequest patientRegisterRequest =
                new PatientRegisterRequest(patientCreateRequest, cameraSelectRequest);

        Admin admin = null;
        given(patientService.createAndConnectPatient(patientCreateRequest, admin))
                .willReturn(createPatient());

        mockMvc.perform(
                        post("/api/admin/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patientRegisterRequest))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.message").value("요청 성공 및 리소스 생성됨"));
    }
}
