package aurora.carevisionapiserver.domain.bed.converter;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.dto.BedRequest.BedCreateRequest;
import aurora.carevisionapiserver.domain.bed.dto.BedResponse.BedInfoResponse;
import aurora.carevisionapiserver.domain.hospital.domain.Department;

public class BedConverter {
    public static Bed toBed(BedCreateRequest bedCreateRequest, Department department) {
        return Bed.builder()
                .inpatientWardNumber(bedCreateRequest.getInpatientWardNumber())
                .patientRoomNumber(bedCreateRequest.getPatientRoomNumber())
                .bedNumber(bedCreateRequest.getBedNumber())
                .department(department)
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
