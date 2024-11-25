package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;

public interface CustomCameraRepository {
    List<Camera> sortByBedInfo(long adminId, long departmentId);
}
