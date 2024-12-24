package aurora.carevisionapiserver.domain.bed.service;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.dto.BedRequest.BedCreateRequest;

public interface BedService {
    Bed findBed(BedCreateRequest bedCreateRequest);

    Bed findById(Long id);
}
