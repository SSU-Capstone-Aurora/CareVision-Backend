package aurora.carevisionapiserver.domain.camera.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.camera.domain.Camera;

public interface CameraRepository extends JpaRepository<Camera, String>, CameraRepositoryCustom {
    Optional<Camera> findByPatient_Id(Long patient_id);
}
