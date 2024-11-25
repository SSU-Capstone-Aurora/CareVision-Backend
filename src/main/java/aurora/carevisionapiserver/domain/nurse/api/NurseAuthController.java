package aurora.carevisionapiserver.domain.nurse.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.DepartmentSelectRequest;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseSignUpRequest;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseInfoResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.auth.dto.request.AuthRequest.LoginRequest;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.TokenResponse;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import aurora.carevisionapiserver.global.security.handler.annotation.ExtractToken;
import aurora.carevisionapiserver.global.util.validation.annotation.IsActivateNurse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth 🔐", description = "인증 관련 API")
@RestController
@Validated
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
    public BaseResponse<NurseInfoResponse> createNurse(
            @RequestBody NurseSignUpRequest nurseSignUpRequest) {

        String username = nurseSignUpRequest.getNurse().getUsername();
        authService.validateUsername(username);

        NurseCreateRequest nurseCreateRequest = nurseSignUpRequest.getNurse();
        DepartmentSelectRequest departmentSelectRequest = nurseSignUpRequest.getDepartment();

        Department department = hospitalService.getDepartment(departmentSelectRequest.getId());
        Nurse nurse = nurseService.createNurse(nurseCreateRequest, department);

        return BaseResponse.onSuccess(NurseConverter.toNurseInfoResponse(nurse));
    }

    @Operation(summary = "간호사 회원가입 중복 체크 API", description = "주어진 아이디가 이미 존재하는지 확인합니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "AUTH200", description = "OK, 성공"),
        @ApiResponse(responseCode = "AUTH400", description = "아이디가 이미 존재합니다.")
    })
    @GetMapping("/check-username")
    public BaseResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean isDuplicated = authService.isUsernameDuplicated(username);

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
            description =
                    "간호사가 서비스에 로그인합니다_예림 Response Body에 accessToken을, Cookie에 refreshToken을 발급합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "AUTH404", description = "인증에 실패했습니다."),
        @ApiResponse(responseCode = "AUTH407", description = "승인되지 않은 유저입니다."),
    })
    @PostMapping("/login")
    public BaseResponse<TokenResponse> login(
            @RequestBody @IsActivateNurse LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        authService.validateUsername(username, Role.NURSE);

        return authService
                .authenticate(username, password)
                .map(authentication -> BaseResponse.onSuccess(authService.generateTokens(username)))
                .orElse(
                        BaseResponse.onFailure(
                                ErrorStatus.INVALID_CREDENTIALS.getCode(),
                                ErrorStatus.INVALID_CREDENTIALS.getMessage(),
                                null));
    }

    @Operation(
            summary = "간호사 로그아웃 API",
            description = "간호사가 서비스에 로그아웃합니다. DB에 있는 리프레시 토큰 삭제를 위해 refresh 토큰을 받습니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
        @ApiResponse(responseCode = "AUTH404", description = "인증에 실패했습니다.")
    })
    @PostMapping("/logout")
    public BaseResponse<TokenResponse> logout(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @RequestHeader("refreshToken") @ExtractToken String refreshToken) {
        authService.logout(nurse.getId(), refreshToken);
        return BaseResponse.of(SuccessStatus._NO_CONTENT, null);
    }
}
