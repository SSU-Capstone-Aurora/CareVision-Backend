package aurora.carevisionapiserver.domain.camera.converter;

import static aurora.carevisionapiserver.domain.bed.converter.BedConverter.toBedInfoResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoListResponse;
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

    public static StreamingListResponse toStreamingListResponse(
            Map<Patient, String> streamingInfo) {

        List<StreamingResponse> responses =
                streamingInfo.entrySet().stream()
                        .map(entry -> toStreamingResponse(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());

        return StreamingListResponse.builder()
                .streamingResponse(responses)
                .totalCount((long) responses.size())
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

    public static VideoInfoListResponse toVideoInfoListResponse(
            List<VideoInfoResponse> videoInfoResponses) {
        return VideoInfoListResponse.builder()
                .videoInfoList(videoInfoResponses)
                .totalCount((long) videoInfoResponses.size())
                .build();
    }

    public static VideoInfoResponse toVideoInfoResponse(
            Video video, String thumbnail, String duration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return VideoInfoResponse.builder()
                .videoId(video.getId())
                .thumbnail(thumbnail)
                .name(video.getCreatedAt().format(formatter))
                .length(duration)
                .build();
    }

    public static VideoLinkResponse toVideoLinkRespose(Video video) {
        return VideoLinkResponse.builder().link(video.getLink()).build();
    }
}
