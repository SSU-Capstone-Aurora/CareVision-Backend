package aurora.carevisionapiserver.global.fcm.dto;

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
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmInfoResponse {
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
