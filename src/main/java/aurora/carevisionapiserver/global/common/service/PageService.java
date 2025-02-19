package aurora.carevisionapiserver.global.common.service;

import java.util.List;

import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface PageService {
    Long getNextCursor(int size, List<Patient> patients);
}
