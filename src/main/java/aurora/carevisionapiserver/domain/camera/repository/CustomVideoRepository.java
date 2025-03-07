package aurora.carevisionapiserver.domain.camera.repository;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CustomVideoRepository {
    Slice<Video> findByPatient(Patient patient, Long lastIdx, int size);
}
