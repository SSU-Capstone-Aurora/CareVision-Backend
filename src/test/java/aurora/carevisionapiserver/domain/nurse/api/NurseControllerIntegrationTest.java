package aurora.carevisionapiserver.domain.nurse.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;
import aurora.carevisionapiserver.global.common.service.PageService;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;

@AutoConfigureMockMvc
public class NurseControllerIntegrationTest extends IntegrationTestSupport {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired PatientRepository patientRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired BedRepository bedRepository;
    @MockBean PatientService patientService;
    @MockBean PageService pageService;

    @WithMockUser(
            username = "nurse",
            roles = {"NURSE"})
    @DisplayName("담당하는 환자 내역을 조회한다.")
    @Test
    void getPatientList() throws Exception {
        // given
        PageRequest request = new PageRequest(-1L, 2);

        Department department = createDepartment();
        Nurse nurse = createNurse(department);
        Bed bed = createBed(department);

        Patient patient = createPatient(1L, department, nurse, bed);
        Slice<Patient> patientSlice = new SliceImpl<>(List.of(patient));

        // when
        when(patientService.getPatientSlice(any(Nurse.class), any())).thenReturn(patientSlice);
        when(pageService.getNextCursor(any(), any())).thenReturn(1L);

        // then
        mockMvc.perform(
                        get("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getCode()))
                .andExpect(jsonPath("$.result.patients").isArray())
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.nextCursor").value(1L));
    }

    private Patient createPatient(Long id, Department department, Nurse nurse, Bed bed) {
        Patient patient =
                Patient.builder().id(id).bed(bed).department(department).nurse(nurse).build();
        return patientRepository.save(patient);
    }

    private Department createDepartment() {
        Hospital hospital = createHospital();
        Department department = Department.builder().name("department").hospital(hospital).build();
        return departmentRepository.save(department);
    }

    private Hospital createHospital() {
        Hospital hospital = Hospital.builder().name("hospital").build();
        return hospitalRepository.save(hospital);
    }

    private Bed createBed(Department department) {
        Bed bed =
                Bed.builder()
                        .bedNumber(1L)
                        .patientRoomNumber(2L)
                        .inpatientWardNumber(3L)
                        .department(department)
                        .build();
        return bedRepository.save(bed);
    }

    private Nurse createNurse(Department department) {
        Nurse nurse =
                Nurse.builder().department(department).username("nurse").isActivated(true).build();
        return nurseRepository.save(nurse);
    }
}
