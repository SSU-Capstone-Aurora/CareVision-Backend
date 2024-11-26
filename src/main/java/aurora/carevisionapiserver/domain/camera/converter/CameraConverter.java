package aurora.carevisionapiserver.domain.camera.converter;

import static aurora.carevisionapiserver.domain.bed.converter.BedConverter.toBedInfoResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
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

    public static StreamingInfoListResponse toStreamingInfoListResponse(
            Map<Patient, String> streamingInfos) {
        return StreamingInfoListResponse.builder()
                .streamingInfoList(
                        streamingInfos.entrySet().stream()
                                .map(
                                        streamingInfo ->
                                                toStreamingInfoResponse(
                                                        streamingInfo.getValue(),
                                                        streamingInfo.getKey()))
                                .collect(Collectors.toList()))
                .totalCount((long) streamingInfos.size())
                .build();
    }
}
