package aurora.carevisionapiserver.global.fcm.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.fcm.dto.FcmClientRequest;
import aurora.carevisionapiserver.global.fcm.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "FCM 🔥", description = "Firebase 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {
    private final FcmService fcmService;
    private final PatientService patientService;

    @Operation(summary = "클라이언트 토큰 등록 API", description = "클라이언트 토큰을 저장합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "FCM400", description = "BAD_REQUEST, 토큰이 만료되었습니다"),
    })
    @PostMapping("/registeration-token")
    public BaseResponse saveClientToken(@RequestBody FcmClientRequest fcmClientRequest) {
        fcmService.saveClientToken(fcmClientRequest);
        return BaseResponse.onSuccess("클라이언트 토큰 저장 완료");
    }

    @Operation(summary = "이상행동 감지 알람 전송 API", description = "환자의 이상행동을 감지하고 알림을 보냅니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "FCM400", description = "BAD_REQUEST, 토큰이 만료되었습니다"),
    })
    @PostMapping("/alarm/{patientId}")
    public BaseResponse sendAlarm(@PathVariable(name = "patientId") Long patientId) {
        Patient patient = patientService.getPatient(patientId);
        String token = fcmService.findClientToken(patient.getNurse());

        fcmService.abnormalBehaviorAlarm(patient, patient.getNurse().getId(), token);

        return BaseResponse.onSuccess("이상행동 감지 알림 전송 완료");
    }
}
