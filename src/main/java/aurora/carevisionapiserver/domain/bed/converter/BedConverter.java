package aurora.carevisionapiserver.domain.bed.converter;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.dto.BedRequest.BedCreateRequest;

public class BedConverter {
    public static Bed toBed(BedCreateRequest bedCreateRequest) {
        return Bed.builder()
                .inpatientWardNumber(bedCreateRequest.getInpatientWardNumber())
                .patientRoomNumber(bedCreateRequest.getPatientRoomNumber())
                .bedNumber(bedCreateRequest.getBedNumber())
                .build();
    }
}
