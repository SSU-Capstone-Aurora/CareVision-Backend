package aurora.carevisionapiserver.domain.camera.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.camera.repository.CameraRepository;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.exception.CameraException;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {
    private final CameraRepository cameraRepository;

    public List<Camera> getCameras() {
        return cameraRepository.sortByBedInfo();
    }

    @Override
    public void connectPatient(CameraSelectRequest cameraSelectRequest, Patient patient) {
        Camera camera = getCameraFromRequest(cameraSelectRequest);
        camera.assignPatient(patient);
        cameraRepository.save(camera);
    }

    private Camera getCameraFromRequest(CameraSelectRequest cameraSelectRequest) {
        String cameraId = cameraSelectRequest.getId();
        return cameraRepository
                .findById(cameraId)
                .orElseThrow(() -> new CameraException(ErrorStatus.CAMERA_NOT_FOUND));
    }
}
