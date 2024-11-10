package aurora.carevisionapiserver.util;

import aurora.carevisionapiserver.domain.bed.domain.Bed;

public class BedUtils {
    public static Bed createBed() {
        return Bed.builder()
                .id(1L)
                .inpatientWardNumber(1L)
                .patientRoomNumber(2L)
                .bedNumber(3L)
                .build();
    }
}
