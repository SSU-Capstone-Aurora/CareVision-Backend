package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByPatient(Patient patient);
}
