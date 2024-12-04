package aurora.carevisionapiserver.domain.camera.service;

import java.util.List;
import java.util.Map;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoResponse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.auth.domain.User;

public interface CameraService {
    List<Camera> getAllCameraInfo(Admin admin);

    List<Camera> getCameraInfoUnlinkedToPatient(User user);

    String getStreamingUrl(Patient patient);

    Map<Patient, String> getStreamingInfo(List<Patient> patients);

    List<VideoInfoResponse> getSavedVideoInfos(Long patientId);

    Video getSavedVideo(Long videoId);

    Video getVideo(Long videoId);
}
