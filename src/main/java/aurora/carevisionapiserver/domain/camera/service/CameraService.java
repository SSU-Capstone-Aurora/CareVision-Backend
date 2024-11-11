package aurora.carevisionapiserver.domain.camera.service;

import java.util.List;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CameraService {
    List<Camera> getCameras();

    void connectPatient(CameraSelectRequest cameraSelectRequest, Patient patient);
}
