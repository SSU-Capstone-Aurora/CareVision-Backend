package aurora.carevisionapiserver.global.elasticsearch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;
import aurora.carevisionapiserver.domain.nurse.repository.NurseEsRepository;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;
import aurora.carevisionapiserver.domain.patient.repository.PatientEsRepository;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;

class ElasticSearchSyncServiceTest extends IntegrationTestSupport {

    @Autowired ElasticSearchSyncService elasticSearchSyncService;

    @Autowired HospitalRepository hospitalRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired BedRepository bedRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired PatientRepository patientRepository;

    @Autowired PatientEsRepository patientEsRepository;
    @Autowired NurseEsRepository nurseEsRepository;

    private Department department;

    @BeforeEach
    void setup() {
        department = createDepartment("오로라 과");
    }

    @AfterEach
    void tearDown() {
        patientEsRepository.deleteAll();
        nurseEsRepository.deleteAll();
        patientRepository.deleteAllInBatch();
        nurseRepository.deleteAllInBatch();
        bedRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
    }

    @DisplayName("환자 데이터 Elasticsearch와 동기화")
    @Test
    void syncPatientDatabaseToElasticsearch() {
        // given
        createPatient(1L, "patient1", "A10000");
        createPatient(2L, "patient2", "B10000");
        createPatient(3L, "patient3", "C10000");

        // when
        elasticSearchSyncService.syncPatientDatabaseToElasticsearch();

        // then
        Iterable<PatientDocument> patientDocuments = patientEsRepository.findAll();
        assertThat(patientDocuments)
                .hasSize(3)
                .extracting(
                        "patientName",
                        "code",
                        "inpatientWardNumber",
                        "patientRoomNumber",
                        "bedNumber")
                .containsExactlyInAnyOrder(
                        tuple("patient1", "A10000", 1L, 2L, 3L),
                        tuple("patient2", "B10000", 1L, 2L, 3L),
                        tuple("patient3", "C10000", 1L, 2L, 3L));
    }

    @DisplayName("간호사 데이터 Elasticsearch와 동기화")
    @Test
    void syncNurseDatabaseToElasticsearch() {
        // given
        createNurse(1L, "nurse1", "aurora1");
        createNurse(2L, "nurse2", "aurora2");
        createNurse(3L, "nurse3", "aurora3");

        // when
        elasticSearchSyncService.syncNurseDatabaseToElasticsearch();

        // then
        Iterable<NurseDocument> nurseDocuments = nurseEsRepository.findAll();
        assertThat(nurseDocuments)
                .hasSize(3)
                .extracting("name", "username")
                .containsExactlyInAnyOrder(
                        tuple("nurse1", "aurora1"),
                        tuple("nurse2", "aurora2"),
                        tuple("nurse3", "aurora3"));
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

    private void createPatient(Long id, String name, String code) {
        Bed bed = createBed(1L, 1L, 2L, 3L, department);
        Nurse nurse = createNurse(id, "nurse1", "aurora1");
        Patient patient =
                patientRepository.save(
                        Patient.builder()
                                .id(id)
                                .name(name)
                                .code(code)
                                .department(department)
                                .bed(bed)
                                .nurse(nurse)
                                .build());
        bed.registerPatient(patient);
    }

    private Nurse createNurse(Long id, String name, String username) {
        return nurseRepository.save(
                Nurse.builder()
                        .id(id)
                        .name(name)
                        .username(username)
                        .department(department)
                        .build());
    }
}
