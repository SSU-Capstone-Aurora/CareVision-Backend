package aurora.carevisionapiserver.global.auth.api;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.LoginResponse;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import aurora.carevisionapiserver.global.security.handler.annotation.RefreshTokenApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Controller
@ResponseBody
@RequiredArgsConstructor
@Tag(name = "Auth 🔐", description = "인증 관련 API")
public class ReissueController {
    private final AuthService authService;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Operation(
            summary = "관리자 refresh 및 access 토큰 재발급 API",
            description =
                    "access token이 만료되면 Authoriazation 헤더에 refresh token을 넣어 요청하여 access 토큰을 body에 재발급합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @PostMapping("/api/admin/reissue")
    public BaseResponse<LoginResponse> reissueForAdmin(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        LoginResponse loginResponse = authService.handleReissue(authorizationHeader);
        return BaseResponse.of(SuccessStatus.REFRESH_TOKEN_ISSUED, loginResponse);
    }

    @Operation(
            summary = "간호사 refresh 토큰 재발급 API",
            description = "refresh 토큰이 만료된 경우 refresh 토큰과 access 토큰을 body에 재발급합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @PostMapping("/api/reissue")
    public BaseResponse<LoginResponse> reissueForNurse(
            @Parameter(name = "admin", hidden = true) @AuthUser Nurse nurse,
            HttpServletRequest request) {
        String refreshToken = request.getHeader(AUTHORIZATION_HEADER);
        System.out.println(refreshToken);
        LoginResponse loginResponse = authService.handleReissue(refreshToken);
        return BaseResponse.of(SuccessStatus.REFRESH_TOKEN_ISSUED, loginResponse);
    }
}
