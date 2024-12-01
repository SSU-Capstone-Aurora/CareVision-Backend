package aurora.carevisionapiserver.domain.camera.service.Impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.repository.CameraRepository;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.exception.CameraException;
import aurora.carevisionapiserver.global.auth.domain.User;
import aurora.carevisionapiserver.global.auth.service.S3Service;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {
    private static final int CAMERA_IP_INDEX = 0;
    private static final int CAMERA_PW_INDEX = 1;
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
        patient.getBed().registerCamera(camera);
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
        Long patientId = patient.getId();
        return s3Service.getRecentImage(patientId);
    }

    private List<String> getCameraInfoLinkedToPatient(Patient patient) {
        Camera camera =
                cameraRepository
                        .findByPatient(patient)
                        .orElseThrow(() -> new CameraException(ErrorStatus.CAMERA_NOT_FOUND));
        return List.of(camera.getIp(), camera.getPassword());
    }
}
