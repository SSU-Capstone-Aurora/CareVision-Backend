package aurora.carevisionapiserver.global.fcm.dto;

import lombok.Builder;
import lombok.Getter;

public class AlarmResponse {
    @Builder
    @Getter
    public static class AlarmData {
        private long patientId;
        private String patientName;
        private long inpatientWardNumber;
        private long patientRoomNumber;
        private long bedNumber;
    }
}
