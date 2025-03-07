package aurora.carevisionapiserver.global.common.service;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.global.common.domain.Identifiable;
import aurora.carevisionapiserver.global.common.dto.request.PageForCameraRequest;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;

public interface PageService {
    <T extends Identifiable> Long getNextCursor(PageRequest request, List<T> entities);

    String getNextCursorForCameras(PageForCameraRequest request, List<Camera> cameras);
}
