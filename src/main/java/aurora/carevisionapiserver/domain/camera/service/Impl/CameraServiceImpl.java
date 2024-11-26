package aurora.carevisionapiserver.domain.camera.service.Impl;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.repository.CameraRepository;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.exception.CameraException;
import aurora.carevisionapiserver.global.auth.domain.User;
import aurora.carevisionapiserver.global.auth.service.S3Service;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.UriFormatter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {
    private static final int CAMERA_IP_INDEX = 0;
    private static final int CAMERA_PW_INDEX = 1;
    private static final String S3_KEY = "s3_url";
    private final S3Service s3Service;

    @Value("${camera.streaming.url}")
    String urlFormat;

    private final CameraRepository cameraRepository;

    public List<Camera> getAllCameraInfo(Admin admin) {
        return cameraRepository.findAllCamerasSortedByBed(admin.getDepartment().getId());
    }

    public List<Camera> getCameraInfoUnlinkedToPatient(User user) {
        return cameraRepository.findCamerasUnlinkedToPatientSortedByBed(
                user.getDepartment().getId());
    }

    @Override
    @Transactional
    public void connectPatient(Camera camera, Patient patient) {
        camera.getBed().registerPatient(patient);
    }

    @Override
    public Camera getCamera(String cameraId) {
        return cameraRepository
                .findById(cameraId)
                .orElseThrow(() -> new CameraException(ErrorStatus.CAMERA_NOT_FOUND));
    }

    @Override
    public String getStreamingUrl(Patient patient) {
        List<String> cameraInfo = getCameraInfoLinkedToPatient(patient);
        return String.format(
                urlFormat, cameraInfo.get(CAMERA_IP_INDEX), cameraInfo.get(CAMERA_PW_INDEX));
    }

    @Override
    public Map<Patient, String> getStreamingInfo(List<Patient> patients) {
        return patients.stream().collect(Collectors.toMap(patient -> patient, this::getThumbnail));
    }

    private String getThumbnail(Patient patient) {
        String rtspUrl = getStreamingUrl(patient);
        Long patientId = patient.getId();
        URI requestUrl = UriFormatter.getThumbnailUrl(rtspUrl, patientId.toString());
        if (requestUrl == null) {
            return s3Service.getRecentImage(patientId);
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString(S3_KEY);
        } else {
            return s3Service.getRecentImage(patientId);
        }
    }

    private List<String> getCameraInfoLinkedToPatient(Patient patient) {
        Camera camera =
                cameraRepository
                        .findByPatient(patient)
                        .orElseThrow(() -> new CameraException(ErrorStatus.CAMERA_NOT_FOUND));
        return List.of(camera.getIp(), camera.getPassword());
    }
}
