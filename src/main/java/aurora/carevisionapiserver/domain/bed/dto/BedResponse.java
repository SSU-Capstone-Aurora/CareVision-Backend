package aurora.carevisionapiserver.domain.bed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BedResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BedInfoResponse {
        private Long inpatientWardNumber;
        private Long patientRoomNumber;
        private Long bedNumber;
    }
}
