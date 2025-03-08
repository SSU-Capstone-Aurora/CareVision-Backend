package aurora.carevisionapiserver.global.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.global.common.domain.Identifiable;
import aurora.carevisionapiserver.global.common.service.PageService;

@Service
public class PageServiceImpl implements PageService {
    @Override
    public <T extends Identifiable> Long getNextCursor(List<T> entities) {
        return entities.get(entities.size() - 1).getId();
    }

    @Override
    public String getNextCursorForCameras(List<Camera> cameras) {
        return cameras.get(cameras.size() - 1).getId();
    }
}
