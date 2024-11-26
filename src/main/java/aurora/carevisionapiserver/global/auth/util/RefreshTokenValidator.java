package aurora.carevisionapiserver.global.auth.util;

import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.global.auth.exception.AuthException;
import aurora.carevisionapiserver.global.auth.repository.RefreshTokenRepository;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefreshTokenValidator {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public void validateToken(String refreshToken) {
        jwtUtil.isValidToken(refreshToken);
    }

    public void validateTokenOwnerId(String refreshToken) {
        String username = jwtUtil.getId(refreshToken);
        refreshTokenRepository
                .findByUsername(username)
                .orElseThrow(() -> new AuthException(ErrorStatus.INVALID_REFRESH_TOKEN));
    }
}
