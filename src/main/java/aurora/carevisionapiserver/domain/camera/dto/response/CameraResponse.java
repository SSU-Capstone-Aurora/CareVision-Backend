package aurora.carevisionapiserver.domain.camera.dto.response;

import java.util.List;

import aurora.carevisionapiserver.domain.bed.dto.BedResponse.BedInfoResponse;
import lombok.Builder;
import lombok.Getter;

public class CameraResponse {
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

    @Getter
    @Builder
    public static class StreamingInfoResponse {
        String url;
        String patientName;
        BedInfoResponse bedInfo;
    }

    @Getter
    @Builder
    public static class StreamingInfoListResponse {
        List<StreamingInfoResponse> streamingInfoList;
        Long totalCount;
    }
}
