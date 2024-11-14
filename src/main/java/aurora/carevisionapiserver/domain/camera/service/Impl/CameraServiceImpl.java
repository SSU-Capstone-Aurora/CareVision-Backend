package aurora.carevisionapiserver.domain.camera.service.Impl;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
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
    @Transactional
    public void connectPatient(Camera camera, Patient patient) {
        camera.registerPatient(patient);
        cameraRepository.save(camera);
    }

    @Override
    public Camera getCamera(String cameraId) {
        return cameraRepository
                .findById(cameraId)
                .orElseThrow(() -> new CameraException(ErrorStatus.CAMERA_NOT_FOUND));
    }
}
