package aurora.carevisionapiserver.domain.camera.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CameraRepository extends JpaRepository<Camera, String>, CustomCameraRepository {
    @Query("SELECT c FROM Camera c JOIN c.bed b WHERE b.patient = :patient")
    Optional<Camera> findByPatient(@Param("patient") Patient patient);
}
