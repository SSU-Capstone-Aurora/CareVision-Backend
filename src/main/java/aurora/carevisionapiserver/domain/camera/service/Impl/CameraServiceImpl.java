package aurora.carevisionapiserver.domain.camera.service.Impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.camera.converter.CameraConverter;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoResponse;
import aurora.carevisionapiserver.domain.camera.exception.CameraException;
import aurora.carevisionapiserver.domain.camera.repository.CameraRepository;
import aurora.carevisionapiserver.domain.camera.repository.VideoRepository;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.auth.domain.User;
import aurora.carevisionapiserver.global.infra.aws.S3Service;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {
    private static final int CAMERA_IP_INDEX = 0;
    private static final int CAMERA_PW_INDEX = 1;

    private final S3Service s3Service;
    private final PatientService patientService;

    @Value("${camera.streaming.url}")
    String urlFormat;

    private final CameraRepository cameraRepository;
    private final VideoRepository videoRepository;

    public List<Camera> getAllCameraInfo(Admin admin) {
        return cameraRepository.findAllCamerasSortedByBed(admin.getDepartment().getId());
    }

    public List<Camera> getCameraInfoUnlinkedToPatient(User user) {
        return cameraRepository.findCamerasUnlinkedToPatientSortedByBed(
                user.getDepartment().getId());
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

    @Override
    public List<VideoInfoResponse> getSavedVideoInfos(Long patientId) {
        Patient patient = patientService.getPatient(patientId);
        List<Video> videos = videoRepository.findByPatient(patient);
        return videos.stream().map(this::createVideoInfoResponse).collect(Collectors.toList());
    }

    @Override
    public Video getSavedVideo(Long videoId) {
        return getVideo(videoId);
    }

    @Override
    public Video getVideo(Long videoId) {
        return videoRepository
                .findById(videoId)
                .orElseThrow(() -> new CameraException(ErrorStatus.VIDEO_NOT_FOUND));
    }

    private VideoInfoResponse createVideoInfoResponse(Video video) {
        String duration = s3Service.getVideoDuration(video.getLink());
        String thumbnail =
                s3Service.getSavedVideoThumbnail(video.getPatient().getId(), video.getId());
        return CameraConverter.toVideoInfoResponse(video, thumbnail, duration);
    }

    private String getThumbnail(Patient patient) {
        Long patientId = patient.getId();
        return s3Service.getRecentImage(patientId);
    }

    private List<String> getCameraInfoLinkedToPatient(Patient patient) {
        Camera camera =
                cameraRepository
                        .findByPatient(patient)
                        .orElseThrow(
                                () ->
                                        new aurora.carevisionapiserver.domain.patient.exception
                                                .CameraException(ErrorStatus.CAMERA_NOT_FOUND));
        return List.of(camera.getIp(), camera.getPassword());
    }
}
