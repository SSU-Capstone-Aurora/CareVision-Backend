package aurora.carevisionapiserver.domain.patient.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCodeRequest;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientNameResponse;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.auth.domain.User;
import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import aurora.carevisionapiserver.global.security.handler.annotation.RefreshTokenApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Common-Patient 🤒", description = "공통 - 환자 등록 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @Operation(
            summary = "환자 번호를 통한 환자 조회 API",
            description =
                    "환자 코드를 입력하여 환자명을 조회합니다. 환자 코드를 보내주면 랜덤으로 이름을 생성합니다. 환자 코드는 민감한 개인 정보를 포함할 수 있으므로 POST 요청을 통해 전송됩니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @PostMapping("/name")
    public BaseResponse<PatientNameResponse> getPatientNameByCode(
            @Parameter(name = "user", hidden = true) @AuthUser User user,
            @RequestBody PatientCodeRequest patientCodeRequest) {
        String patientCode = patientCodeRequest.getCode();
        String patientName = patientService.getPatientNameByCode(patientCode);
        return BaseResponse.onSuccess(PatientConverter.toPatientNameResponse(patientName));
    }
}
