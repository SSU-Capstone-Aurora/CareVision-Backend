package aurora.carevisionapiserver.controller;

import static org.mockito.BDDMockito.given;
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

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.patient.api.AdminPatientController;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.service.PatientService;

@WebMvcTest(AdminPatientController.class)
class AdminPatientControllerTest {

    @MockBean private PatientService patientService;
    @Autowired private MockMvc mockMvc;

    private Patient createPatient() {
        Bed bed =
                Bed.builder()
                        .id(1L)
                        .inpatientWardNumber(1L)
                        .patientRoomNumber(2L)
                        .bedNumber(3L)
                        .build();
        return Patient.builder().id(1L).name("test").code("kk-123").bed(bed).build();
    }

    private Patient createOtherPatient() {
        Bed bed =
                Bed.builder()
                        .id(2L)
                        .inpatientWardNumber(2L)
                        .patientRoomNumber(3L)
                        .bedNumber(3L)
                        .build();
        return Patient.builder().id(2L).name("kangrok").code("rr-123").bed(bed).build();
    }

    @Test
    @WithMockUser
    @DisplayName("환자를 검색에 성공한다.")
    void searchPatientSuccess() throws Exception {
        String patientName = "test";
        List<Patient> patients = List.of(createPatient(), createOtherPatient());
        given(patientService.searchPatient(patientName)).willReturn(patients);

        mockMvc.perform(
                        get("/api/admin/patients")
                                .param("patientName", patientName)
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
}
