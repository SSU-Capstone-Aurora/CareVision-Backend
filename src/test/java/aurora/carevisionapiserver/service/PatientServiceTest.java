package aurora.carevisionapiserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.service.impl.AdminServiceImpl;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.Impl.PatientServiceImpl;
import aurora.carevisionapiserver.util.NurseUtils;
import aurora.carevisionapiserver.util.PatientUtil;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @InjectMocks private PatientServiceImpl patientService;
    @Mock private PatientRepository patientRepository;
    @Mock private AdminServiceImpl adminService;

    @Test
    @DisplayName("환자명 검색에 성공한다.")
    void searchPatientSuccess() {
        // given
        String patientName = "test";
        List<Patient> patients =
                List.of(PatientUtil.createPatient(), PatientUtil.createOtherPatient());
        given(patientRepository.searchByName(patientName)).willReturn(patients);
        // when
        List<Patient> result = patientService.searchPatient(patientName);
        // then
        assertEquals(patients.size(), result.size());
        assertEquals(
                patients.get(0).getBed().getInpatientWardNumber(),
                result.get(0).getBed().getInpatientWardNumber());
        assertEquals(patients.get(0).getName(), result.get(0).getName());
        assertEquals(
                patients.get(1).getBed().getInpatientWardNumber(),
                result.get(1).getBed().getInpatientWardNumber());
        assertEquals(patients.get(1).getName(), result.get(1).getName());
    }

    @Test
    @DisplayName("환자가 없는 경우 예외 처리한다.")
    void searchPatientFail() {
        // given
        given(patientRepository.searchByName("test")).willReturn(Collections.emptyList());

        // when & then
        assertThrows(PatientException.class, () -> patientService.searchPatient("test"));
    }

    @Test
    @DisplayName("간호사로 환자를 검색한다.")
    void searchPatientByNurse() {
        // given
        Nurse nurse = NurseUtils.createActiveNurse();
        List<Patient> patients = nurse.getPatients();
        given(patientRepository.findPatientByNurse(nurse)).willReturn(nurse.getPatients());

        // when
        List<Patient> result = patientService.getPatients(nurse);

        // then
        assertEquals(result.size(), patients.size());
        assertEquals(result.get(0).getName(), patients.get(0).getName());
        assertEquals(result.get(0).getId(), patients.get(0).getId());
        assertEquals(result.get(0).getCode(), patients.get(0).getCode());
    }

    @Test
    @DisplayName("관리자의 환자를 조회합니다.")
    void getPatientByAdmin() {
        Nurse nurse = NurseUtils.createActiveNurse();

        Hospital hospital = Hospital.builder().id(1L).name(nurse.getHospital().getName()).build();
        Admin admin = Admin.builder().id(1L).username("admin1").hospital(hospital).build();

        Patient patient = PatientUtil.createPatient();
        Patient otherPatient = PatientUtil.createOtherPatient();

        // Given
        List<Patient> patients = Arrays.asList(patient, otherPatient);

        // When
        when(adminService.getAdmin(admin.getId())).thenReturn(admin);
        when(patientRepository.findPatientByAdmin(admin)).thenReturn(patients);

        List<Patient> actualPatients = patientService.getPatients(admin.getId());

        // Then
        assertEquals(patients, actualPatients);
        verify(adminService, times(1)).getAdmin(admin.getId());
        verify(patientRepository, times(1)).findPatientByAdmin(admin);
    }

    @Test
    @DisplayName("간호사와 환자를 연결한다.")
    void registerNurse() {
        Nurse nurse = NurseUtils.createActiveNurse();
        Patient patient = Patient.builder().name("테스트").code("12E").build();

        assertNull(patient.getNurse());

        when(patientRepository.findPatientByCode(patient.getCode())).thenReturn(patient);
        when(patientService.getPatientsByPatientId(patient.getCode())).thenReturn(patient);

        String patientName = patientService.registerNurse(nurse, patient.getCode());

        assertEquals(nurse, patient.getNurse());
        assertEquals("테스트", patientName);
    }
}
