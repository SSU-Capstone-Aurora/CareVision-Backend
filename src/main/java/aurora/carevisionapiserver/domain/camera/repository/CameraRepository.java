package aurora.carevisionapiserver.domain.camera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.camera.domain.Camera;

public interface CameraRepository extends JpaRepository<Camera, String>, CameraRepositoryCustom {}
