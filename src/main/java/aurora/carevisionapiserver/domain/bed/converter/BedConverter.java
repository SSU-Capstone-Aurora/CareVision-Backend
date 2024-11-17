package aurora.carevisionapiserver.domain.bed.converter;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.dto.BedRequest.BedCreateRequest;
import aurora.carevisionapiserver.domain.bed.dto.BedResponse.BedInfoResponse;

public class BedConverter {
    public static Bed toBed(BedCreateRequest bedCreateRequest) {
        return Bed.builder()
                .inpatientWardNumber(bedCreateRequest.getInpatientWardNumber())
                .patientRoomNumber(bedCreateRequest.getPatientRoomNumber())
                .bedNumber(bedCreateRequest.getBedNumber())
                .build();
    }

    public static BedInfoResponse toBedInfoResponse(Bed bed) {
        return BedInfoResponse.builder()
                .inpatientWardNumber(bed.getInpatientWardNumber())
                .patientRoomNumber(bed.getPatientRoomNumber())
                .bedNumber(bed.getBedNumber())
                .build();
    }
}
