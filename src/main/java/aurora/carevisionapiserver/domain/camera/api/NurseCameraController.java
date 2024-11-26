package aurora.carevisionapiserver.domain.camera.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.camera.converter.CameraConverter;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.CameraInfoListResponse;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Nurse-Camera 📷", description = "간호사 - 카메라 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cameras")
public class NurseCameraController {

    private final CameraService cameraService;

    @Operation(summary = "환자와 연결되지 않은 카메라 목록 조회 API", description = "환자와 연결되지 않은 카메라 목록 조회합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("/unlinked")
    public BaseResponse<CameraInfoListResponse> getUnlinkedCameras(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        List<Camera> cameras = cameraService.getCameraInfoUnlinkedToPatient(nurse);
        return BaseResponse.of(
                SuccessStatus._OK, CameraConverter.toCameraInfoListResponse(cameras));
    }
}
