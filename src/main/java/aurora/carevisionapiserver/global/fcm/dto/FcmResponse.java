package aurora.carevisionapiserver.global.fcm.dto;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FcmResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FireStoreResponse {
        private long patientId;
        private String patientName;
        private int inpatientWardNumber;
        private int patientRoomNumber;
        private int bedNumber;
        private Timestamp time;
    }
}
