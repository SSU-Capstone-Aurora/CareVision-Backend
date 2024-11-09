package aurora.carevisionapiserver.global.auth.api;

import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aurora.carevisionapiserver.global.auth.domain.RefreshToken;
import aurora.carevisionapiserver.global.auth.exception.AuthException;
import aurora.carevisionapiserver.global.auth.repository.RefreshTokenRepository;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Controller
@ResponseBody
@RequiredArgsConstructor
@Tag(name = "Auth 🔐", description = "인증 관련 API")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Operation(
            summary = "refresh 토큰 재발급 API",
            description = "refresh 토큰이 만료된 경우 refresh 토큰을 재발급합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @PostMapping("/api/reissue")
    public BaseResponse<?> reissueForNurse(
            HttpServletRequest request, HttpServletResponse response) {
        String role = authService.getCurrentUserRole();
        return handleReissue(request, response, role);
    }

    private BaseResponse<?> handleReissue(
            HttpServletRequest request, HttpServletResponse response, String role) {
        // get refresh token
        Cookie[] cookies = request.getCookies();
        String refreshToken = cookies[0].getValue();

        if (cookies == null) {
            return BaseResponse.onFailure(
                    ErrorStatus.REFRESH_TOKEN_NULL.getCode(),
                    ErrorStatus.REFRESH_TOKEN_NULL.getMessage(),
                    null);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {
            return BaseResponse.onFailure(
                    ErrorStatus.REFRESH_TOKEN_NULL.getCode(),
                    ErrorStatus.REFRESH_TOKEN_NULL.getMessage(),
                    null);
        }

        // expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return BaseResponse.onFailure(
                    ErrorStatus.REFRESH_TOKEN_EXPIRED.getCode(),
                    ErrorStatus.REFRESH_TOKEN_EXPIRED.getMessage(),
                    null);
        }

        // 토큰이 refresh인지 검사
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refreshToken")) {
            return BaseResponse.onFailure(
                    ErrorStatus.INVALID_REFRESH_TOKEN.getCode(),
                    ErrorStatus.INVALID_REFRESH_TOKEN.getMessage(),
                    null);
        }

        String username = jwtUtil.getUsername(refreshToken);

        try {
            refreshTokenRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new AuthException(ErrorStatus.INVALID_REFRESH_TOKEN));
        } catch (AuthException e) {
            return BaseResponse.onFailure(
                    ErrorStatus.INVALID_REFRESH_TOKEN.getCode(),
                    ErrorStatus.INVALID_REFRESH_TOKEN.getMessage(),
                    null);
        }

        // 이전 refresh token 삭제
        refreshTokenRepository.deleteByUsername(username);

        String newRefreshToken =
                jwtUtil.createJwt("refreshToken", username, role, refreshExpirationTime);

        // refresh token 업데이트
        addRefreshToken(username, newRefreshToken, refreshExpirationTime);

        // response 헤더 설정
        response.addCookie(createCookie("refreshToken", newRefreshToken));

        return BaseResponse.of(SuccessStatus.REFRESH_TOKEN_ISSUED, null);
    }

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public void addRefreshToken(String username, String refreshToken, long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshToken newRefreshToken =
                RefreshToken.builder()
                        .username(username)
                        .refreshToken(refreshToken)
                        .expiration(date.toString())
                        .build();
        refreshTokenRepository.save(newRefreshToken);
    }
}
