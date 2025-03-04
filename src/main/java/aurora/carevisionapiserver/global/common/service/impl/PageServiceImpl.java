package aurora.carevisionapiserver.global.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.global.common.domain.Identifiable;
import aurora.carevisionapiserver.global.common.service.PageService;

@Service
public class PageServiceImpl implements PageService {
    private static final Long DEFAULT_CURSOR = -1L;

    @Override
    public <T extends Identifiable> Long getNextCursor(int size, List<T> entities) {
        Long nextCursor = DEFAULT_CURSOR;
        if (entities.size() == size) {
            nextCursor = entities.get(size - 1).getId();
        }
        return nextCursor;
    }
}
