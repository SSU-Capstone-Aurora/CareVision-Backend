package aurora.carevisionapiserver.domain.patient.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.converter.PatientDocumentConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;

class PatientEsRepositoryTest extends IntegrationTestSupport {
    @Autowired PatientEsRepository patientEsRepository;

    private Department department;
    private Bed bed;

    @BeforeEach
    void setup() {
        department = createDepartment("오로라 과");
        bed = createBed(1L, 1L, 2L, 3L, department);
    }

    @AfterEach
    void tearDown() {
        patientEsRepository.deleteAll();
    }

    @DisplayName("환자 이름으로 아직 간호사와 연결되지 않은 환자를 검색한다.")
    @Test
    void searchByNameAndNurseIsNull() {
        // given
        Patient linkedPatient1 = createPatient(1L, "patient1", "A10000", true);
        Patient linkedPatient2 = createPatient(2L, "patient2", "B10000", true);
        Patient unlinkedPatient1 = createPatient(3L, "patient3", "D10000", false);
        Patient unlinkedPatient2 = createPatient(4L, "patient4", "E10000", false);

        patientEsRepository.saveAll(
                PatientDocumentConverter.toPatientDocumentList(
                        List.of(
                                linkedPatient1,
                                linkedPatient2,
                                unlinkedPatient1,
                                unlinkedPatient2)));

        String patientName = "patient";

        // when
        List<PatientDocument> response =
                patientEsRepository.searchByNameAndNurseIsNull(patientName);

        // then
        assertThat(response)
                .hasSize(2)
                .extracting("patientId", "name")
                .contains(tuple(3L, "patient3"), tuple(4L, "patient4"));
    }

    private Department createDepartment(String name) {
        Hospital hospital = createHospital();
        return Department.builder().hospital(hospital).name(name).build();
    }

    private Hospital createHospital() {
        return Hospital.builder().name("hospital").build();
    }

    private Bed createBed(
            Long id,
            Long inpatientWardNumber,
            Long patientRoomNumber,
            Long bedNumber,
            Department department) {
        return Bed.builder()
                .id(id)
                .inpatientWardNumber(inpatientWardNumber)
                .patientRoomNumber(patientRoomNumber)
                .bedNumber(bedNumber)
                .department(department)
                .build();
    }

    private Patient createPatient(Long id, String name, String code, boolean linkedToNurse) {
        return Patient.builder()
                .id(id)
                .name(name)
                .code(code)
                .department(department)
                .bed(bed)
                .nurse(linkedToNurse ? createNurse(id, "오로라", department) : null)
                .build();
    }

    private Nurse createNurse(Long id, String name, Department department) {
        return Nurse.builder().id(id).name(name).department(department).build();
    }
}
