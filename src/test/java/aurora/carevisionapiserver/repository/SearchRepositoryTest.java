package aurora.carevisionapiserver.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.common.repository.SearchRepository;

@ExtendWith(MockitoExtension.class)
public class SearchRepositoryTest {

    @Mock private SearchRepository<Nurse> nurseSearchRepository;
    @Mock private SearchRepository<Patient> patientSearchRepository;

    @Test
    @DisplayName("환자를 검색했을 때 결과가 있으면 환자 리스트를 반환한다.")
    void searchByNamePatientTest() {
        // given
        String name = "가나";
        Patient patient1 = Patient.builder().name(name + "다").build();
        Patient patient2 = Patient.builder().name(name + "라").build();

        when(patientSearchRepository.searchByName(name)).thenReturn(List.of(patient1, patient2));
        when(patientSearchRepository.searchByName("라")).thenReturn(List.of(patient2));

        // when
        List<Patient> result1 = patientSearchRepository.searchByName(name);
        List<Patient> result2 = patientSearchRepository.searchByName("라");

        // then1
        assertNotNull(result1);
        assertEquals(2, result1.size());
        assertEquals(patient1.getName(), result1.get(0).getName());
        assertEquals(patient2.getName(), result1.get(1).getName());

        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals(patient2.getName(), result2.get(0).getName());
    }

    @Test
    @DisplayName("간호사를 검색했을 때 결과가 있으면 간호사 리스트를 반환한다.")
    void searchByNameNurseTest() {
        // given
        String name = "가나";
        Nurse nurse1 = Nurse.builder().name(name + "다").build();
        Nurse nurse2 = Nurse.builder().name(name + "라").build();

        when(nurseSearchRepository.searchByName(name)).thenReturn(List.of(nurse1, nurse2));
        when(nurseSearchRepository.searchByName("라")).thenReturn(List.of(nurse2));

        // when
        List<Nurse> result1 = nurseSearchRepository.searchByName(name);
        List<Nurse> result2 = nurseSearchRepository.searchByName("라");

        // then1
        assertNotNull(result1);
        assertEquals(2, result1.size());
        assertEquals(nurse1.getName(), result1.get(0).getName());
        assertEquals(nurse2.getName(), result1.get(1).getName());

        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals(nurse2.getName(), result2.get(0).getName());
    }
}
