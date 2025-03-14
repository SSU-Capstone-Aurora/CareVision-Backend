package aurora.carevisionapiserver.domain.bed.repository;

import java.util.List;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.hospital.domain.Department;

public interface CustomBedRepository {
    List<Bed> findNextBeds(Department department, Bed lastBed, int size);
}
