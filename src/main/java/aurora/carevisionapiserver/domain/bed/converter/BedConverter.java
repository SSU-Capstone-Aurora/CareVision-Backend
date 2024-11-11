package aurora.carevisionapiserver.domain.bed.converter;

import java.util.Map;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.exception.BedException;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.ParseUtil;

public class BedConverter {
    public static Bed toBed(String bedInfo) {
        try {
            Map<String, Long> bedInfoMap = ParseUtil.parseBedInfo(bedInfo);
            return Bed.builder()
                    .inpatientWardNumber(bedInfoMap.get("inpatientWardNumber"))
                    .patientRoomNumber(bedInfoMap.get("patientRoomNumber"))
                    .bedNumber(bedInfoMap.get("bedNumber"))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new BedException(ErrorStatus.INVALID_BED_INFO);
        }
    }
}
