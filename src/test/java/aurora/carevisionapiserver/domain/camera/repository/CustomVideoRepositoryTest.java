package aurora.carevisionapiserver.domain.camera.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;

class CustomVideoRepositoryTest extends IntegrationTestSupport {
    @Autowired PatientRepository patientRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired BedRepository bedRepository;
    @Autowired VideoRepository videoRepository;
    @Autowired CustomVideoRepositoryImpl customVideoRepository;

    @AfterEach
    void tearDown() {
        videoRepository.deleteAllInBatch();
        patientRepository.deleteAllInBatch();
        nurseRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
        bedRepository.deleteAllInBatch();
    }

    @DisplayName("환자의 비디오 정보를 페이지 처리해 처음 조회한다.")
    @Test
    void findByPatientFirst() {
        // given
        Department department = createDepartment();
        Nurse nurse = createNurse(department);
        Patient patient = createPatient(department, nurse);

        Video video1 = createVideo("video1", patient);
        Video video2 = createVideo("video2", patient);

        Long lastIdx = -1L;
        int size = 2;

        // when
        Slice<Video> response = customVideoRepository.findByPatient(patient, lastIdx, size);

        // then
        assertThat(response).hasSize(size);
        assertThat(response.hasNext()).isFalse();
        assertThat(response).extracting("name").contains(video1.getName(), video2.getName());
    }

    @DisplayName("환자의 비디오 정보를 페이지 처리해 조회한다. 요청된 마지막 커서 값이 1L이며 조회되는 개수는 2개이다.")
    @Test
    void findByPatient() {
        // given
        Department department = createDepartment();
        Nurse nurse = createNurse(department);
        Patient patient = createPatient(department, nurse);

        Video video1 = createVideo("video1", patient);
        Video video2 = createVideo("video2", patient);
        Video video3 = createVideo("video3", patient);
        Video video4 = createVideo("video4", patient);

        Long lastIdx = 1L;
        int size = 2;

        // when
        Slice<Video> response = customVideoRepository.findByPatient(patient, lastIdx, size);

        // then
        assertThat(response).hasSize(size);
        assertThat(response.hasNext()).isTrue();
        assertThat(response).extracting("name").contains(video2.getName(), video3.getName());
    }

    private Video createVideo(String name, Patient patient) {
        Video video = Video.builder().name(name).patient(patient).build();
        return videoRepository.save(video);
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
        Nurse nurse = Nurse.builder().department(department).build();
        return nurseRepository.save(nurse);
    }

    private Patient createPatient(Department department, Nurse nurse) {
        Patient patient = Patient.builder().nurse(nurse).department(department).build();
        return patientRepository.save(patient);
    }
}
