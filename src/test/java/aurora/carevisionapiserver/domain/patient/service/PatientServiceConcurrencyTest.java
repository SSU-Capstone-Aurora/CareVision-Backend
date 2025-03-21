package aurora.carevisionapiserver.domain.patient.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import aurora.carevisionapiserver.IntegrationTestSupport;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.dto.BedRequest.BedCreateRequest;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;

class PatientServiceConcurrencyTest extends IntegrationTestSupport {
    @Autowired PatientRepository patientRepository;
    @Autowired NurseRepository nurseRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired HospitalRepository hospitalRepository;
    @Autowired BedRepository bedRepository;
    @Autowired PatientService patientService;
    @Autowired NurseService nurseService;
    @PersistenceContext EntityManager em;

    private final int THREAD_COUNT = 10;

    @AfterEach
    void tearDown() {
        bedRepository.deleteAllInBatch();
        patientRepository.deleteAllInBatch();
        nurseRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        hospitalRepository.deleteAllInBatch();
    }

    @DisplayName("새로운 담당 환자를 등록하는 과정의 동시성 시나리오를 검증하며, 해당 케이스는 통과한다.")
    @Test
    void createAndConnectPatient() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        Department department = createDepartment();
        Bed bed = createBed(department);
        BedCreateRequest bedRequest =
                BedCreateRequest.builder()
                        .bedNumber(bed.getBedNumber())
                        .patientRoomNumber(bed.getPatientRoomNumber())
                        .inpatientWardNumber(bed.getInpatientWardNumber())
                        .build();
        PatientCreateRequest request =
                PatientCreateRequest.builder()
                        .name("patient")
                        .code("code1")
                        .bed(bedRequest)
                        .build();
        Nurse nurse = createNurse(department);

        String jpql = "SELECT p FROM Patient p WHERE p.name = :name";

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(
                    () -> {
                        try {
                            patientService.createAndConnectPatient(request, nurse);
                        } catch (Exception e) {
                            System.out.println("Exception occurred: " + e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
        }

        latch.await();
        executorService.shutdown();

        List<Patient> patients =
                em.createQuery(jpql, Patient.class)
                        .setParameter("name", request.getName())
                        .getResultList();

        // then
        assertEquals(1, patients.size());
    }

    private Bed createBed(Department department) {
        Bed bed =
                Bed.builder()
                        .bedNumber(1L)
                        .patientRoomNumber(2L)
                        .inpatientWardNumber(3L)
                        .department(department)
                        .build();
        return bedRepository.save(bed);
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
}
