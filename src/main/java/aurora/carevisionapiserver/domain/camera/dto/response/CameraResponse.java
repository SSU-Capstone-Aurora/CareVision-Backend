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
    public static class StreamingResponse {
        long patientId;
        String thumbnail;
        String patientName;
        BedInfoResponse bedInfo;
    }

    @Getter
    @Builder
    public static class StreamingPageResponse {
        List<StreamingResponse> streamingResponse;
        boolean hasNext;
        Long nextCursor;
    }

    @Getter
    @Builder
    public static class VideoInfoListResponse {
        List<VideoInfoResponse> videoInfoList;
        Long totalCount;
    }

    @Getter
    @Builder
    public static class VideoInfoResponse {
        Long videoId;
        String thumbnail;
        String name;
        String length;
    }

    @Getter
    @Builder
    public static class VideoLinkResponse {
        String link;
    }
}
