package aurora.carevisionapiserver.domain.camera.converter;

import static aurora.carevisionapiserver.domain.bed.converter.BedConverter.toBedInfoResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoPageResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingPageResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoPageResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoLinkResponse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public class CameraConverter {
    public static CameraInfoListResponse toCameraInfoListResponse(List<Camera> cameras) {
        List<CameraInfoResponse> cameraInfoListResponse =
                cameras.stream().map(CameraConverter::toCameraInfoResponse).toList();
        return CameraInfoListResponse.builder()
                .cameraInfoList(cameraInfoListResponse)
                .totalCount((long) cameraInfoListResponse.size())
                .build();
    }

    public static CameraInfoPageResponse toCameraInfoPageResponse(
            Slice<Camera> cameras, String nextCursor) {
        List<CameraInfoResponse> cameraInfoListResponse =
                cameras.getContent().stream().map(CameraConverter::toCameraInfoResponse).toList();
        return CameraInfoPageResponse.builder()
                .cameraInfoList(cameraInfoListResponse)
                .hasNext(cameras.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

    public static CameraInfoResponse toCameraInfoResponse(Camera camera) {
        return CameraInfoResponse.builder()
                .cameraId(camera.getId())
                .inpatientWardNumber(camera.getBed().getInpatientWardNumber())
                .patientRoomNumber(camera.getBed().getPatientRoomNumber())
                .bedNumber(camera.getBed().getBedNumber())
                .build();
    }

    public static StreamingInfoResponse toStreamingInfoResponse(String url, Patient patient) {
        return StreamingInfoResponse.builder()
                .url(url)
                .patientName(patient.getName())
                .bedInfo(toBedInfoResponse(patient.getBed()))
                .build();
    }

    private static StreamingResponse toStreamingResponse(Patient patient, String thumbnail) {
        return StreamingResponse.builder()
                .patientId(patient.getId())
                .patientName(patient.getName())
                .thumbnail(thumbnail)
                .bedInfo(toBedInfoResponse(patient.getBed()))
                .build();
    }

    public static StreamingPageResponse toStreamingPageResponse(
            Map<Patient, String> streamingInfo, boolean hasNext, Long nextCursor) {
        List<StreamingResponse> responses =
                streamingInfo.entrySet().stream()
                        .map(entry -> toStreamingResponse(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());

        return StreamingPageResponse.builder()
                .streamingResponse(responses)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    public static VideoInfoPageResponse toVideoInfoPageResponse(
            Slice<VideoInfoResponse> videoInfo, Long nextCursor) {
        return VideoInfoPageResponse.builder()
                .videoInfoList(videoInfo.getContent())
                .hasNext(videoInfo.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

    public static VideoInfoResponse toVideoInfoResponse(
            Video video, String thumbnail, String duration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return VideoInfoResponse.builder()
                .id(video.getId())
                .thumbnail(thumbnail)
                .name(video.getCreatedAt().format(formatter))
                .length(duration)
                .build();
    }

    public static VideoLinkResponse toVideoLinkRespose(Video video) {
        return VideoLinkResponse.builder().link(video.getLink()).build();
    }
}
