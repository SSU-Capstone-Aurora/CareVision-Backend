package aurora.carevisionapiserver.domain.camera.converter;

import static aurora.carevisionapiserver.domain.bed.converter.BedConverter.toBedInfoResponse;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoResponse;
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
                .inpatientWardNumber(camera.getPatient().getBed().getInpatientWardNumber())
                .patientRoomNumber(camera.getPatient().getBed().getPatientRoomNumber())
                .bedNumber(camera.getPatient().getBed().getBedNumber())
                .build();
    }

    public static StreamingInfoResponse toStreamingInfoResponse(String url, Patient patient) {
        return StreamingInfoResponse.builder()
                .url(url)
                .patientName(patient.getName())
                .bedInfo(toBedInfoResponse(patient.getBed()))
                .build();
    }
}
