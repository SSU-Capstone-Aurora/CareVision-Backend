package aurora.carevisionapiserver.domain.camera.service;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;

public interface CameraService {
    List<Camera> getCameras();
}
