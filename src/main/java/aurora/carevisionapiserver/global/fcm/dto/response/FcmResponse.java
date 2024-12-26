package aurora.carevisionapiserver.global.fcm.dto.response;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FcmResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FireStoreResponse {
        @Setter private String documentId;
        private long patientId;
        private String patientName;
        private int inpatientWardNumber;
        private int patientRoomNumber;
        private int bedNumber;
        private Timestamp time;
        private boolean read;
    }
}
