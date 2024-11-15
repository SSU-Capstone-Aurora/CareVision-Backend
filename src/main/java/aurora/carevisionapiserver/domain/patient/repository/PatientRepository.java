package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.common.repository.SearchRepository;

public interface PatientRepository
        extends JpaRepository<Patient, Long>, SearchRepository<Patient>, CustomPatientRepository {
    List<Patient> findPatientByNurse(Nurse nurse);

    Patient findPatientByCode(String patientCode);

    boolean existsByCode(String code);
}
