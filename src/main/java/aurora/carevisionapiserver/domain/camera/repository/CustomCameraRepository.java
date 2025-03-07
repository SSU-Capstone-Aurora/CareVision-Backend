package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.hospital.domain.Department;

public interface CustomCameraRepository {
    Slice<Camera> findAllCamerasSortedByBed(Department department, Long lastIdx, int size);

    List<Camera> findCamerasUnlinkedToPatientSortedByBed(long departmentId);
}
