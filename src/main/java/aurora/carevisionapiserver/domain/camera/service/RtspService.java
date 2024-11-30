package aurora.carevisionapiserver.domain.camera.service;

import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface RtspService {
    Boolean requestThumbnailUrl(Patient patient);
}
