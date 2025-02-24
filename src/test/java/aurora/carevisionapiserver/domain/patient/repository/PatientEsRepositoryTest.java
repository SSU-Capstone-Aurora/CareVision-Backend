package aurora.carevisionapiserver.domain.patient.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.converter.PatientDocumentConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataElasticsearchTest
class PatientEsRepositoryTest {
    @Autowired PatientEsRepository patientEsRepository;

    private List<Patient> patientList;

    @BeforeAll
    void setup() {

        Department department1 = createDepartment("오로라 과");
        Bed bed1 = createBed(1L, 2L, 3L, department1);
        Nurse nurse1 = createNurse(1L, "오로라", department1);

        Patient patient1 = createPatient(1L, "patient0", department1, nurse1, bed1);
        Patient patient2 = createPatient(2L, "patient1", department1, nurse1, bed1);
        Patient patient3 = createPatient(3L, "patient2", department1, nurse1, bed1);

        Patient unlinkedPatient1 = createPatient(4L, "patient3", department1, null, bed1);
        Patient unlinkedPatient2 = createPatient(5L, "patient4", department1, null, bed1);

        patientList = List.of(patient1, patient2, patient3, unlinkedPatient1, unlinkedPatient2);

        patientEsRepository.saveAll(PatientDocumentConverter.toPatientDocumentList(patientList));
    }

    @AfterEach
    void tearDown() {
        patientEsRepository.deleteAll();
    }

    @DisplayName("환자 이름으로 아직 간호사와 연결되지 않은 환자를 검색한다.")
    @Test
    void searchByNameAndNurseIsNull() {
        // given
        String patientName = "patient";

        // when
        List<PatientDocument> response =
                patientEsRepository.searchByNameAndNurseIsNull(patientName);

        // then
        assertThat(response)
                .hasSize(2)
                .extracting("patientId", "name")
                .contains(tuple(4L, "patient3"), tuple(5L, "patient4"));
    }

    private Department createDepartment(String name) {
        Hospital hospital = createHospital();
        return Department.builder().hospital(hospital).name(name).build();
    }

    private Hospital createHospital() {
        return Hospital.builder().name("hospital").build();
    }

    private Bed createBed(
            Long inpatientWardNumber,
            Long patientRoomNumber,
            Long bedNumber,
            Department department) {
        return Bed.builder()
                .bedNumber(inpatientWardNumber)
                .patientRoomNumber(inpatientWardNumber)
                .inpatientWardNumber(inpatientWardNumber)
                .department(department)
                .build();
    }

    private Nurse createNurse(Long id, String name, Department department) {
        return Nurse.builder().id(id).name(name).department(department).build();
    }

    private Patient createPatient(
            Long id, String name, Department department, Nurse nurse, Bed bed) {
        return Patient.builder()
                .id(id)
                .name(name)
                .department(department)
                .nurse(nurse)
                .bed(bed)
                .build();
    }
}
