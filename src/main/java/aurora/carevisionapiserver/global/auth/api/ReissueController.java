package aurora.carevisionapiserver.global.auth.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.TokenResponse;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import aurora.carevisionapiserver.global.security.handler.annotation.ExtractToken;
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

    @Operation(
            summary = "관리자 refresh 및 access 토큰 재발급 API",
            description = "refresh token과 access 토큰을 body에 재발급합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @GetMapping("/api/admin/reissue")
    public BaseResponse<TokenResponse> reissueForAdmin(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @Parameter(hidden = true) @ExtractToken String refreshToken) {
        TokenResponse tokenResponse = authService.handleReissue(refreshToken);
        return BaseResponse.of(SuccessStatus.REFRESH_TOKEN_ISSUED, tokenResponse);
    }

    @Operation(
            summary = "간호사 refresh 및 access 토큰 재발급 API",
            description = "refresh token과 access 토큰을 body에 재발급합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @GetMapping("/api/reissue")
    public BaseResponse<TokenResponse> reissueForNurse(
            @Parameter(name = "admin", hidden = true) @AuthUser Nurse nurse,
            @Parameter(hidden = true) @ExtractToken String refreshToken) {
        TokenResponse tokenResponse = authService.handleReissue(refreshToken);
        return BaseResponse.of(SuccessStatus.REFRESH_TOKEN_ISSUED, tokenResponse);
    }
}
