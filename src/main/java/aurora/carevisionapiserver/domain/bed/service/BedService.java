package aurora.carevisionapiserver.domain.bed.service;

import java.util.List;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.dto.BedRequest.BedCreateRequest;
import aurora.carevisionapiserver.domain.hospital.domain.Department;

public interface BedService {
    Bed findBed(BedCreateRequest bedCreateRequest);

    Bed findById(Long id);

    List<Bed> findNextBeds(Department department, Bed lastBed, int size);
}
