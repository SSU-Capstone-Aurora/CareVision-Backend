package aurora.carevisionapiserver.domain.nurse.api;

import static aurora.carevisionapiserver.domain.nurse.converter.NurseConverter.toNurseLoginResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalSelectRequest;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseSignUpRequest;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseInfoResponse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseLoginResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth 🔐", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NurseAuthController {
    private final NurseService nurseService;
    private final HospitalService hospitalService;
    private final AuthService authService;

    @Operation(summary = "간호사 회원가입 API", description = "간호사가 회원가입합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "HOSPITAL400", description = "NOT_FOUND, 병원을 찾을 수 없습니다."),
    })
    @PostMapping("/sign-up")
    public BaseResponse<NurseInfoResponse> createAdmin(
            @RequestBody NurseSignUpRequest nurseSignUpRequest) {

        NurseCreateRequest nurseCreateRequest = nurseSignUpRequest.getNurse();
        HospitalSelectRequest hospitalSelectRequest = nurseSignUpRequest.getHospital();

        Hospital hospital = hospitalService.getHospital(hospitalSelectRequest.getId());
        Nurse nurse = nurseService.createNurse(nurseCreateRequest, hospital);

        return BaseResponse.onSuccess(NurseConverter.toNurseInfoResponse(nurse));
    }

    @Operation(summary = "간호사 회원가입 중복 체크", description = "주어진 아이디가 이미 존재하는지 확인합니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "AUTH200", description = "OK, 성공"),
        @ApiResponse(responseCode = "AUTH400", description = "아이디가 이미 존재합니다.")
    })
    @GetMapping("/check-username")
    public BaseResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean isDuplicated = nurseService.isUsernameDuplicated(username);

        if (isDuplicated) {
            return BaseResponse.onFailure(
                    ErrorStatus.USERNAME_DUPLICATED.getCode(),
                    ErrorStatus.USERNAME_DUPLICATED.getMessage(),
                    false);
        } else {
            return BaseResponse.of(SuccessStatus.USERNAME_AVAILABLE, true);
        }
    }

    @Operation(
            summary = "간호사 로그인 API",
            description = "간호사가 서비스에 로그인합니다. body에 accessToken을, header에 refreshToken을 발급합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "AUTH404", description = "인증에 실패했습니다.")
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<NurseLoginResponse>> login(
            @RequestBody NurseRequest.NurseLoginRequest nurseLoginRequest,
            HttpServletResponse response) {

        String username = nurseLoginRequest.getUsername();
        String password = nurseLoginRequest.getPassword();

        return authService
                .authenticate(username, password)
                .map(
                        authentication -> {
                            String accessToken = authService.createAccessToken(username, "NURSE");
                            String refreshToken =
                                    authService.createRefreshToken(username, "NURSE");

                            response.addCookie(authService.createRefreshTokenCookie(refreshToken));

                            return ResponseEntity.ok(
                                    BaseResponse.onSuccess(toNurseLoginResponse(accessToken)));
                        })
                .orElse(
                        ResponseEntity.status(ErrorStatus.INVALID_CREDENTIALS.getHttpStatus())
                                .body(
                                        BaseResponse.onFailure(
                                                ErrorStatus.INVALID_CREDENTIALS.getCode(),
                                                ErrorStatus.INVALID_CREDENTIALS.getMessage(),
                                                null)));
    }
}
