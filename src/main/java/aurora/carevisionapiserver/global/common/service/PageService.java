package aurora.carevisionapiserver.global.common.service;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.global.common.domain.Identifiable;

public interface PageService {
    <T extends Identifiable> Long getNextCursor(int size, List<T> entities);

    String getNextCursorForCameras(int size, List<Camera> cameras);
}
