package aurora.carevisionapiserver.domain.camera.service;

import java.util.List;
import java.util.Map;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CameraService {
    Camera getCamera(String cameraId);

    List<Camera> getCameras();

    void connectPatient(Camera cameraSelectRequest, Patient patient);

    String getStreamingUrl(Long patientId);

    Map<Patient, String> getStreamingInfo(List<Patient> patients);
}
