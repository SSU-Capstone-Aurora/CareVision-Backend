package aurora.carevisionapiserver.domain.admin.api;

import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.admin.converter.AdminConverter;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminSignUpRequest;
import aurora.carevisionapiserver.domain.admin.dto.response.AdminResponse.AdminSignUpResponse;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.auth.dto.request.AuthRequest.LoginRequest;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.TokenResponse;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import aurora.carevisionapiserver.global.security.handler.annotation.ExtractToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth 🔐", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminAuthController {
    private final AdminService adminService;
    private final AuthService authService;

    @Operation(summary = "관리자 회원가입 API", description = "관리자가 회원가입합니다_예림")
    @ApiResponses({@ApiResponse(responseCode = "AUTH200", description = "OK, 성공")})
    @PostMapping("/sign-up")
    public BaseResponse<AdminSignUpResponse> createAdmin(
            @RequestBody AdminSignUpRequest adminSignUpRequest) {
        Admin admin = adminService.signup(adminSignUpRequest);

        return BaseResponse.onSuccess(AdminConverter.toAdminSignUpResponse(admin));
    }

    @Operation(summary = "관리자 회원가입 중복 체크 API", description = "주어진 아이디가 이미 존재하는지 확인합니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
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

    @Operation(summary = "관리자 로그인 API", description = "관리자가 서비스에 로그인합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "AUTH404", description = "인증에 실패했습니다.")
    })
    @PostMapping("/login")
    public BaseResponse<TokenResponse> login(@RequestBody LoginRequest adminLoginRequest) {

        String username = adminLoginRequest.getUsername();

        authService.validateUsername(username, Role.ADMIN);

        String password = adminLoginRequest.getPassword();
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
            summary = "관리자 로그아웃 API",
            description = "관리자가 서비스에 로그아웃합니다. DB에 있는 리프레시 토큰 삭제를 위해 refresh 토큰을 받습니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
        @ApiResponse(responseCode = "AUTH404", description = "인증에 실패했습니다.")
    })
    @PostMapping("/logout")
    public BaseResponse<Object> logout(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @Parameter(hidden = true) @ExtractToken String refreshToken) {
        authService.logout(admin.getId(), refreshToken);
        return BaseResponse.of(SuccessStatus._NO_CONTENT, new HashMap<>());
    }
}
