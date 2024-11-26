package aurora.carevisionapiserver.domain.camera.service;

import java.util.List;
import java.util.Map;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CameraService {
    Camera getCamera(String cameraId);

    List<Camera> getAllCameraInfo(Admin admin);

    List<Camera> getCameraInfoUnlinkedToPatient(Nurse nurse);

    void connectPatient(Camera cameraSelectRequest, Patient patient);

    String getStreamingUrl(Patient patient);

    Map<Patient, String> getStreamingInfo(List<Patient> patients);
}
