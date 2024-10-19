package aurora.carevisionapiserver.domain.camera.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class CameraDTO {
    @Getter
    @Builder
    public static class CameraInfoResponse {
        private String cameraId;
        private Long inpatientWardNumber;
        private Long patientRoomNumber;
        private Long bedNumber;
    }

    @Getter
    @Builder
    public static class CameraInfoListResponse {
        private List<CameraInfoResponse> cameraInfoList;
        private Long totalCount;
    }
}
