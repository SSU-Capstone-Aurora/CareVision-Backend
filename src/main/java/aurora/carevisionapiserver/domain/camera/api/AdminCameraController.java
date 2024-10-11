package aurora.carevisionapiserver.domain.camera.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.camera.converter.CameraConverter;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.CameraDTO.CameraInfoListDTO;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin-Camera 📷", description = "관리자 - 카메라 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/cameras")
public class AdminCameraController {
    private final CameraService cameraService;

    @Operation(summary = "카메라 목록 조회 API", description = "카메라 목록 조회합니다_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("")
    public BaseResponse<CameraInfoListDTO> getCameras() {
        List<Camera> cameras = cameraService.getCameras();
        return BaseResponse.of(SuccessStatus._OK, CameraConverter.toCameraInfoDTOList(cameras));
    }
}
