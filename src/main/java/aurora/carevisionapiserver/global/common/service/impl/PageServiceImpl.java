package aurora.carevisionapiserver.global.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.common.service.PageService;

@Service
public class PageServiceImpl implements PageService {
    private static final Long DEFAULT_CURSOR = -1L;

    @Override
    public Long getNextCursor(int size, List<Patient> patients) {
        Long nextCursor = DEFAULT_CURSOR;
        if (patients.size() == size) {
            nextCursor = patients.get(size - 1).getId();
        }
        return nextCursor;
    }
}
