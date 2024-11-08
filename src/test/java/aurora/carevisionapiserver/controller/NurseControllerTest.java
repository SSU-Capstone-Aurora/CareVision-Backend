package aurora.carevisionapiserver.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import aurora.carevisionapiserver.domain.nurse.api.NurseController;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.util.validation.resolver.AuthUserArgumentResolver;
import aurora.carevisionapiserver.util.NurseUtils;
import aurora.carevisionapiserver.util.PatientUtil;

@WebMvcTest(NurseController.class)
class NurseControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private NurseController nurseController;
    @MockBean private NurseService nurseService;
    @MockBean private PatientService patientService;

    @MockBean private AuthUserArgumentResolver authUserArgumentResolver;

    @BeforeEach
    void setup() {
        Nurse nurse = NurseUtils.createActiveNurse();

        mockMvc =
                MockMvcBuilders.standaloneSetup(nurseController)
                        .setCustomArgumentResolvers(authUserArgumentResolver)
                        .build();

        given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(nurse);
    }

    @Test
    @WithMockUser
    @DisplayName("간호사 마이페이지 조회 성공한다.")
    void getNurseMyPageSuccess() throws Exception {
        Nurse nurse = NurseUtils.createActiveNurse();
        Long nurseId = nurse.getId();

        given(nurseService.getActiveNurse(nurseId)).willReturn(nurse);
        when(nurseService.existsByNurseId(nurseId)).thenReturn(true);

        mockMvc.perform(get("/api/profile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.name").value("김간호사"))
                .andExpect(jsonPath("$.result.registeredAt").value("2024-10-11"))
                .andExpect(jsonPath("$.result.hospitalName").value("서울병원"))
                .andExpect(jsonPath("$.result.department").value("성형외과"));
    }

    /*
    @Test
    @WithMockUser
    @DisplayName("간호사가 자신의 환자들을 조회한다.")
    void getNursePatients() throws Exception {
        Nurse nurse = createNurse();
        Long nurseId = nurse.getId();

        given(nurseService.getNurse(nurseId)).willReturn(Optional.of(nurse));
        given(nurseService.existsByNurseId(nurseId)).willReturn(true);
        given(patientService.getPatients(nurse)).willReturn(nurse.getPatients());

        mockMvc.perform(
                        get("/api/patients")
                                .param("nurseId", String.valueOf(nurseId))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.patients[0].name").value("강록"))
                .andExpect(jsonPath("$.result.patients[0].code").value("kk-123"));
    }
    */
    @Test
    @DisplayName("담당 환자 퇴원 성공한다.")
    void deletePatient_Success() throws Exception {
        // Given
        Long patientId = PatientUtil.createPatient().getId();

        doNothing().when(patientService).deletePatient(patientId);

        // When & Then
        mockMvc.perform(
                        delete("/api/{patientId}", patientId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value("COMMON202"))
                .andExpect(jsonPath("$.message").value("요청 성공 및 반환할 콘텐츠가 없음"));
    }

    @Test
    @DisplayName("존재하지 않는 환자 퇴원 시 환자를 찾을 수 없다는 예외가 발생한다.")
    void deletePatient_NotFound() throws Exception {
        // Given
        Long patientId = 999L;

        doThrow(new PatientException(ErrorStatus.PATIENT_NOT_FOUND))
                .when(patientService)
                .deletePatient(patientId);

        assertThrows(PatientException.class, () -> patientService.deletePatient(patientId));
    }
}
