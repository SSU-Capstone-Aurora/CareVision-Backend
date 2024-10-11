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

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.Impl.PatientServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @InjectMocks private PatientServiceImpl patientService;
    @Mock private PatientRepository patientRepository;

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
    @DisplayName("환자명 검색에 성공한다.")
    void searchPatientSuccess() {
        // given
        String patientName = "test";
        List<Patient> patients = List.of(createPatient(), createOtherPatient());
        given(patientRepository.searchPatient(patientName)).willReturn(patients);
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
        given(patientRepository.searchPatient("test")).willReturn(Collections.emptyList());
        // when & then
        assertThrows(
                PatientException.class,
                () -> patientRepository.searchPatient("Nonexistent Patient"));
    }
}
