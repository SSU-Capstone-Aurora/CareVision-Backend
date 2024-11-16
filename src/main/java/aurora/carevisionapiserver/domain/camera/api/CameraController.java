package aurora.carevisionapiserver.domain.camera.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.camera.converter.CameraConverter;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.util.validation.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Camera 📷", description = "카메라 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CameraController {
    private final CameraService cameraService;
    private final PatientService patientService;

    @Operation(summary = "특정 환자 실시간 영상 스트리밍 관련 정보 조회 API", description = "환자의 스트리밍 관련 정보를 조회합니다_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "CAMERA400", description = "NOT FOUND, 카메라를 찾을 수 없습니다.")
    })
    @GetMapping("/streaming/{id}")
    public BaseResponse<StreamingInfoResponse> getStreamingInfo(
            @PathVariable(name = "id") Long patient_id) {
        Patient patient = patientService.getPatient(patient_id);
        String cameraUrl = cameraService.getStreamingUrl(patient_id);
        return BaseResponse.of(
                SuccessStatus._OK, CameraConverter.toStreamingInfoResponse(cameraUrl, patient));
    }

    @Operation(
            summary = "전체 담당 환자 실시간 영상 스트리밍 url 조회 API",
            description = "담당하는 환자의 전체 스트리밍 url을 조회합니다_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "CAMERA400", description = "NOT FOUND, 카메라를 찾을 수 없습니다.")
    })
    @GetMapping("/streaming")
    public BaseResponse<List<CameraUrl>> getStreamingUrls(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        List<Patient> patients = patientService.getPatients(nurse);
        List<CameraUrl> streamingUrl = cameraService.getStreamingUrls(patients);
        return BaseResponse.of(SuccessStatus._OK, streamingUrl);
    }
}
