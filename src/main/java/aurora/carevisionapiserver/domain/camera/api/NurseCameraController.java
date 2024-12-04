package aurora.carevisionapiserver.domain.camera.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.camera.converter.CameraConverter;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoListResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoInfoResponse;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.VideoLinkResponse;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Camera - Nurse 📷", description = "간호사 - 카메라 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NurseCameraController {
    private final CameraService cameraService;
    private final PatientService patientService;

    @Operation(summary = "특정 환자 실시간 영상 스트리밍 관련 정보 조회 API", description = "환자의 스트리밍 관련 정보를 조회합니다_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "CAMERA400", description = "NOT FOUND, 카메라를 찾을 수 없습니다.")
    })
    @GetMapping("/streaming/{patientId}")
    public BaseResponse<StreamingInfoResponse> getStreamingInfo(
            @PathVariable(name = "patientId") Long patientId) {
        Patient patient = patientService.getPatient(patientId);
        String cameraUrl = cameraService.getStreamingUrl(patient);
        return BaseResponse.of(
                SuccessStatus._OK, CameraConverter.toStreamingInfoResponse(cameraUrl, patient));
    }

    @Operation(
            summary = "전체 담당 환자 실시간 영상 스트리밍 관련 정보 조회 API",
            description = "담당하는 환자의 전체 스트리밍 관련 정보를 조회합니다_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "CAMERA400", description = "NOT FOUND, 카메라를 찾을 수 없습니다.")
    })
    @GetMapping("/streaming")
    public BaseResponse<StreamingListResponse> getStreamingInfoList(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        List<Patient> patients = patientService.getPatients(nurse);
        Map<Patient, String> streamingInfo = cameraService.getStreamingInfo(patients);
        return BaseResponse.of(
                SuccessStatus._OK, CameraConverter.toStreamingListResponse(streamingInfo));
    }

    @Operation(
            summary = "특정 환자의 저장된 비디오 리스트 조회 API",
            description = "특정 환자의 저장된 비디오 영상 리스트를 조회합니다(저장 일자, 영상 길이, 썸네일)_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "PATIENT400", description = "NOT FOUND, 환자가 없습니다.")
    })
    @GetMapping("/patients/{patientId}/videos")
    public BaseResponse<VideoInfoListResponse> getSavedVideoInfos(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @PathVariable(name = "patientId") Long patientId) {
        List<VideoInfoResponse> videoInfoResponses = cameraService.getSavedVideoInfos(patientId);
        return BaseResponse.of(
                SuccessStatus._OK, CameraConverter.toVideoInfoListResponse(videoInfoResponses));
    }

    @Operation(summary = "특정 환자의 저장된 비디오 상세 조회 API", description = "특정 환자의 저장된 비디오 영상을 상세 조회합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "PATIENT400", description = "NOT FOUND, 환자가 없습니다.")
    })
    @GetMapping("/videos/{videoId}")
    public BaseResponse<VideoLinkResponse> getSavedVideo(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @PathVariable(name = "videoId") Long videoId) {
        Video video = cameraService.getSavedVideo(videoId);
        return BaseResponse.of(SuccessStatus._OK, CameraConverter.toVideoLinkRespose(video));
    }
}
