package aurora.carevisionapiserver.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.repository.CustomPatientRepositoryImpl;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;

@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomPatientRepositoryImplTest extends IntegrationTestSupport {
    @Autowired NurseRepository nurseRepository;
    @Autowired PatientRepository patientRepository;
    @Autowired HospitalRepository hospitalRepository;

    @Autowired DepartmentRepository departmentRepository;

    @Autowired CustomPatientRepositoryImpl customPatientRepository;
    private Nurse nurse1;
    private List<Patient> patientList;

    @BeforeAll
    void setup() {
        Department department = createDepartment("department");
        nurse1 = createNurse("nurse1", department);

        Patient patient0 = createPatient(0L, "patient0", department, nurse1);
        Patient patient1 = createPatient(1L, "patient1", department, nurse1);
        Patient patient2 = createPatient(2L, "patient2", department, nurse1);
        Patient patient3 = createPatient(3L, "patient3", department, nurse1);
        Patient patient4 = createPatient(4L, "patient4", department, nurse1);
        patientList = List.of(patient0, patient1, patient2, patient3, patient4);
        patientRepository.saveAll(patientList);
    }

    @DisplayName("다음에 조회될 환자 리스트의 개수가 입력 사이즈보다 큰 경우 hasNext는 true이다.")
    @Test
    void findPatientByNurseWhenRemainingSizeIsGreaterThanInputSize() {
        // given
        Long lastIdx = 0L;
        int size = 2;

        Patient patientWhoId1 = patientList.get(1);
        Patient patientWhoId2 = patientList.get(2);

        // when
        Slice<Patient> response = customPatientRepository.findPatientByNurse(nurse1, lastIdx, size);

        // then
        assertEquals(2, response.getContent().size());
        assertThat(response.hasNext()).isTrue();
        assertThat(response.getContent())
                .extracting("id", "name")
                .contains(
                        tuple(patientWhoId1.getId(), patientWhoId1.getName()),
                        tuple(patientWhoId2.getId(), patientWhoId2.getName()));
    }

    @DisplayName("입력된 사이즈와 동일한 개수의 항목이 조회될 때 hasNext는 false로 반환된다.")
    @Test
    void findPatientByNurseWhenLeftSizeIsSmallerThanInputSize() {
        // given
        Long lastIdx = 2L;
        int size = 2;

        Patient patientWhoId3 = patientList.get(3);
        Patient patientWhoId4 = patientList.get(4);

        // when
        Slice<Patient> response = customPatientRepository.findPatientByNurse(nurse1, lastIdx, size);

        // then
        assertEquals(2, response.getContent().size());
        assertThat(response.hasNext()).isFalse();
        assertThat(response.getContent())
                .extracting("id", "name")
                .contains(
                        tuple(patientWhoId3.getId(), patientWhoId3.getName()),
                        tuple(patientWhoId4.getId(), patientWhoId4.getName()));
    }

    @DisplayName("다음에 조회될 환자 리스트의 크기가 입력 사이즈보다 작을 경우에도, 조회된 내용은 정상적으로 반환되며 hasNext는 false로 반환된다.")
    @Test
    void findPatientByNurseWhenNoPatientsAfterLastIndex() {
        // given
        Long lastIdx = 3L;
        int size = 2;

        Patient patientWhoId4 = patientList.get(4);

        // when
        Slice<Patient> response = customPatientRepository.findPatientByNurse(nurse1, lastIdx, size);

        // then
        assertEquals(1, response.getContent().size());
        assertThat(response.hasNext()).isFalse();
        assertThat(response.getContent())
                .extracting("id", "name")
                .contains(tuple(patientWhoId4.getId(), patientWhoId4.getName()));
    }

    @DisplayName("다음에 조회될 환자 리스트가 없는 경우 hasNext는 false로 반환한다.")
    @Test
    void findPatientByNurseWhenNoPatientsLeft() {
        // given
        Long lastIdx = 4L;
        int size = 2;

        // when
        Slice<Patient> response = customPatientRepository.findPatientByNurse(nurse1, lastIdx, size);

        // then
        assertEquals(0, response.getContent().size());
        assertThat(response.hasNext()).isFalse();
    }

    private Department createDepartment(String name) {
        Hospital hospital = createHospital();
        Department department = Department.builder().hospital(hospital).name(name).build();
        return departmentRepository.save(department);
    }

    private Hospital createHospital() {
        Hospital hospital = Hospital.builder().name("hospital").build();
        return hospitalRepository.save(hospital);
    }

    private Nurse createNurse(String name, Department department) {
        Nurse nurse = Nurse.builder().name(name).department(department).build();
        return nurseRepository.save(nurse);
    }

    private Patient createPatient(Long id, String name, Department department, Nurse nurse1) {
        return Patient.builder().id(id).name(name).nurse(nurse1).department(department).build();
    }
}
