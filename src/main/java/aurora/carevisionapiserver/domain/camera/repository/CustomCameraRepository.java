package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;

public interface CustomCameraRepository {
    List<Camera> findAllCamerasSortedByBed(long departmentId);

    List<Camera> findCamerasUnlinkedToPatientSortedByBed(long departmentId);
}
