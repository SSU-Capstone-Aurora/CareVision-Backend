package aurora.carevisionapiserver.global.common.service;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.global.common.domain.Identifiable;

public interface PageService {
    <T extends Identifiable> Long getNextCursor(List<T> entities);

    String getNextCursorForCameras(List<Camera> cameras);
}
