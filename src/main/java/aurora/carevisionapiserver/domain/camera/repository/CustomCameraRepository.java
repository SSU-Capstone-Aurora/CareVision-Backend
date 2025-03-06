package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public interface CustomCameraRepository {
    Slice<Camera> findAllCamerasSortedByBed(Hospital hospital, Long lastIdx, int size);

    List<Camera> findCamerasUnlinkedToPatientSortedByBed(long departmentId);
}
