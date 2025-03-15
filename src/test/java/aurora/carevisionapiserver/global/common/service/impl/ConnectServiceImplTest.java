package aurora.carevisionapiserver.global.common.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.global.common.service.ConnectService;

class ConnectServiceImplTest extends IntegrationTestSupport {
    @Autowired PatientRepository patientRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired BedRepository bedRepository;
    @Autowired ConnectService connectService;

    @AfterEach
    void tearDown() {
        bedRepository.deleteAllInBatch();
        patientRepository.deleteAllInBatch();
        nurseRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
    }

    @DisplayName("환자와 간호사를 연결하는데 성공한다.")
    @Test
    void connectNurseToPatient() {
        // given
        Department department = createDepartment();
        Nurse nurse = createNurse(department);
        Patient patient = createPatient(department, nurse);

        // when
        connectService.connectNurseToPatient(patient, nurse);

        // then
        assertEquals(patient.getNurse(), nurse);
        assertThat(nurse.getPatients().contains(patient)).isTrue();
    }

    private Department createDepartment() {
        Hospital hospital = createHospital();
        Department department = Department.builder().hospital(hospital).build();
        return departmentRepository.save(department);
    }

    private Hospital createHospital() {
        Hospital hospital = Hospital.builder().name("hospital").build();
        return hospitalRepository.save(hospital);
    }

    private Nurse createNurse(Department department) {
        Nurse nurse = Nurse.builder().department(department).patients(new ArrayList<>()).build();
        return nurseRepository.save(nurse);
    }

    private Patient createPatient(Department department, Nurse nurse) {
        Patient patient = Patient.builder().nurse(nurse).department(department).build();
        return patientRepository.save(patient);
    }
}
