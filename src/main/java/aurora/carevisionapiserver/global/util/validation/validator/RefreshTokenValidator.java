package aurora.carevisionapiserver.global.util.validation.validator;

import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.global.auth.exception.AuthException;
import aurora.carevisionapiserver.global.auth.repository.RefreshTokenRepository;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefreshTokenValidator {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public void checkIfTokenNull(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(ErrorStatus.REFRESH_TOKEN_NULL);
        }
    }

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
