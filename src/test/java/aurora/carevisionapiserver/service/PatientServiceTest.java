package aurora.carevisionapiserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Nurse nurse = NurseUtils.createNurse();
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
}
