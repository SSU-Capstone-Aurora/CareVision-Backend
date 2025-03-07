package aurora.carevisionapiserver.global.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.global.common.domain.Identifiable;
import aurora.carevisionapiserver.global.common.dto.request.PageForCameraRequest;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;
import aurora.carevisionapiserver.global.common.service.PageService;

@Service
public class PageServiceImpl implements PageService {
    private static final Long DEFAULT_CURSOR = -1L;

    @Override
    public <T extends Identifiable> Long getNextCursor(PageRequest request, List<T> entities) {
        Long nextCursor = DEFAULT_CURSOR;
        if (entities.size() == request.size()) {
            nextCursor = entities.get(request.size() - 1).getId();
        }
        return nextCursor;
    }

    @Override
    public String getNextCursorForCameras(PageForCameraRequest request, List<Camera> cameras) {
        return null;
    }
}
