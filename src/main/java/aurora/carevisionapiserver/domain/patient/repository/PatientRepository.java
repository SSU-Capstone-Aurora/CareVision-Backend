package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query(value = "SELECT * FROM patient p WHERE p.name LIKE %?1%", nativeQuery = true)
    List<Patient> searchPatient(String patientName);
}
