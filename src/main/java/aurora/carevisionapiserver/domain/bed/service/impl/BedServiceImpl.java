package aurora.carevisionapiserver.domain.bed.service.impl;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.dto.BedRequest;
import aurora.carevisionapiserver.domain.bed.exception.BedException;
import aurora.carevisionapiserver.domain.bed.repository.BedRepository;
import aurora.carevisionapiserver.domain.bed.service.BedService;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BedServiceImpl implements BedService {
    private final BedRepository bedRepository;

    @Override
    public Bed findBed(BedRequest.BedCreateRequest bed) {
        return bedRepository
                .findByBedNumberAndInpatientWardNumberAndPatientRoomNumber(
                        bed.getBedNumber(),
                        bed.getInpatientWardNumber(),
                        bed.getPatientRoomNumber())
                .orElseThrow(() -> new BedException(ErrorStatus.BED_NOT_FOUND));
    }

    @Override
    public Bed findById(Long id) {
        return bedRepository
                .findById(id)
                .orElseThrow(() -> new BedException(ErrorStatus.BED_NOT_FOUND));
    }
}
