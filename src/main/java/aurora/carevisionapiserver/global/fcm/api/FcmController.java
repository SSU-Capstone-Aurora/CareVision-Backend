package aurora.carevisionapiserver.global.fcm.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.fcm.dto.FcmClientRequest;
import aurora.carevisionapiserver.global.fcm.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Tag(name = "FCM 🔥", description = "Firebase 관련 API")
public class FcmController {
    private final FcmService alarmService;

    @Operation(summary = "클라이언트 토큰 등록 API", description = "클라이언트 토큰을 저장합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "FCM400", description = "BAD_REQUEST, 토큰이 만료되었습니다"),
    })
    @PostMapping("/send/registeration-token")
    public BaseResponse saveClientToken(@RequestBody FcmClientRequest fcmClientRequest) {
        alarmService.saveClientToken(fcmClientRequest);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }
}
