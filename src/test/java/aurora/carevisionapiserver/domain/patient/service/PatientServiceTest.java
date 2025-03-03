package aurora.carevisionapiserver.domain.patient.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.converter.PatientDocumentConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;
import aurora.carevisionapiserver.domain.patient.repository.PatientEsRepository;

class PatientServiceTest extends IntegrationTestSupport {

    @Autowired PatientService patientService;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired BedRepository bedRepository;
    @Autowired NurseRepository nurseRepository;
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
        nurseRepository.deleteAllInBatch();
        bedRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
    }

    @DisplayName("환자명을 입력받아 간호사와 연결되지 않은 환자만 검색한다.")
    @Test
    void searchUnlinkedPatients() {
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

        // when
        PatientSearchListResponse response = patientService.searchUnlinkedPatients("patient");

        // then
        assertThat(response.getPatientList())
                .hasSize(2)
                .extracting(
                        "patientName",
                        "code",
                        "inpatientWardNumber",
                        "patientRoomNumber",
                        "bedNumber")
                .containsExactlyInAnyOrder(
                        tuple("patient3", "D10000", 1L, 2L, 3L),
                        tuple("patient4", "E10000", 1L, 2L, 3L));
    }

    @DisplayName("검색어를 입력하지 않았을 때에는 간호사와 연결되지 않은 모든 환자가 검색된다.")
    @Test
    void searchUnlinkedPatientsWhenPatientNameIsEmpty() {
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

        // when
        PatientSearchListResponse response = patientService.searchUnlinkedPatients("");

        // then
        assertThat(response.getPatientList())
                .hasSize(2)
                .extracting(
                        "patientName",
                        "code",
                        "inpatientWardNumber",
                        "patientRoomNumber",
                        "bedNumber")
                .containsExactlyInAnyOrder(
                        tuple("patient3", "D10000", 1L, 2L, 3L),
                        tuple("patient4", "E10000", 1L, 2L, 3L));
    }

    private Department createDepartment(String name) {
        Hospital hospital = createHospital();
        return departmentRepository.save(
                Department.builder().hospital(hospital).name(name).build());
    }

    private Hospital createHospital() {
        return hospitalRepository.save(Hospital.builder().name("hospital").build());
    }

    private Bed createBed(
            Long id,
            Long inpatientWardNumber,
            Long patientRoomNumber,
            Long bedNumber,
            Department department) {
        return bedRepository.save(
                Bed.builder()
                        .id(id)
                        .inpatientWardNumber(inpatientWardNumber)
                        .patientRoomNumber(patientRoomNumber)
                        .bedNumber(bedNumber)
                        .department(department)
                        .build());
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
        return nurseRepository.save(
                Nurse.builder().id(id).name(name).department(department).build());
    }
}
