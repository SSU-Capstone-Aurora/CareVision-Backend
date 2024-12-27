package aurora.carevisionapiserver.global.fcm.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AlarmResponse {
    @Builder
    @Getter
    public static class AlarmData {
        private long patientId;
        private String patientName;
        private long inpatientWardNumber;
        private long patientRoomNumber;
        private long bedNumber;
        private boolean read;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmInfoResponse {
        private String documentId;
        private int inpatientWardNumber;
        private int patientRoomNumber;
        private int bedNumber;
        private String patientName;
        private long patientId;
        private String timeAgo;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmInfoListResponse {
        private List<AlarmInfoResponse> alarmInfoList;
        private long totalCount;
    }
}
