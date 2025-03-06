package aurora.carevisionapiserver.domain.camera.dto.response;

import java.util.List;

import aurora.carevisionapiserver.domain.bed.dto.BedResponse.BedInfoResponse;
import aurora.carevisionapiserver.global.common.domain.Identifiable;
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
    public static class CameraInfoPageResponse {
        List<CameraInfoResponse> cameraInfoList;
        boolean hasNext;
        String nextCursor;
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
    public static class VideoInfoPageResponse {
        List<VideoInfoResponse> videoInfoList;
        boolean hasNext;
        Long nextCursor;
    }

    @Getter
    @Builder
    public static class VideoInfoResponse implements Identifiable {
        Long id;
        String thumbnail;
        String name;
        String length;

        @Override
        public Long getId() {
            return this.id;
        }
    }

    @Getter
    @Builder
    public static class VideoLinkResponse {
        String link;
    }
}
