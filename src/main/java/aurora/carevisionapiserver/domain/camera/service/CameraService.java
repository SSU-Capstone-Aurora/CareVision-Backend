package aurora.carevisionapiserver.domain.camera.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoResponse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.auth.domain.User;
import aurora.carevisionapiserver.global.common.dto.request.PageForCameraRequest;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;

public interface CameraService {
    Slice<Camera> getAllCameraInfo(Admin admin, PageForCameraRequest request);

    List<Camera> getCameraInfoUnlinkedToPatient(User user);

    String getStreamingUrl(Patient patient);

    Map<Patient, String> getStreamingInfo(List<Patient> patients);

    Slice<VideoInfoResponse> getSavedVideoInfos(Long patientId, PageRequest request);

    Video getSavedVideo(Long videoId);

    Video getVideo(Long videoId);
}
