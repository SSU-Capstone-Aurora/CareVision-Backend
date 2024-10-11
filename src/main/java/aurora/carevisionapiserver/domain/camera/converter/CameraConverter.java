package aurora.carevisionapiserver.domain.camera.converter;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.CameraDTO.CameraInfoDTO;
import aurora.carevisionapiserver.domain.camera.dto.CameraDTO.CameraInfoListDTO;

public class CameraConverter {
    public static CameraInfoListDTO toCameraInfoDTOList(List<Camera> cameras) {
        List<CameraInfoDTO> cameraInfoDTOList =
                cameras.stream().map(CameraConverter::toCameraInfo).toList();
        return CameraInfoListDTO.builder()
                .cameraInfoList(cameraInfoDTOList)
                .totalCount((long) cameraInfoDTOList.size())
                .build();
    }

    public static CameraInfoDTO toCameraInfo(Camera camera) {
        return CameraInfoDTO.builder()
                .cameraId(camera.getId())
                .inpatientWardNumber(camera.getPatient().getBed().getInpatientWardNumber())
                .patientRoomNumber(camera.getPatient().getBed().getPatientRoomNumber())
                .bedNumber(camera.getPatient().getBed().getBedNumber())
                .build();
    }
}
