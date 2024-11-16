package aurora.carevisionapiserver.domain.camera.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.camera.dto.response.CameraUrl;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "특정 환자 실시간 영상 스트리밍 url 조회 API", description = "환자의 스트리밍 url을 조회합니다_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "CAMERA400", description = "NOT FOUND, 카메라를 찾을 수 없습니다.")
    })
    @GetMapping("/streaming/{id}")
    public BaseResponse<CameraUrl> getStreamingUrl(@PathVariable(name = "id") Long patient_id) {
        String streamingUrl = cameraService.getStreamingUrl(patient_id);
        return BaseResponse.of(SuccessStatus._OK, CameraUrl.from(streamingUrl));
    }
}
