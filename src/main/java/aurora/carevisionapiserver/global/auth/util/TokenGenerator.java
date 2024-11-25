package aurora.carevisionapiserver.global.auth.util;

import java.util.Date;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.global.auth.converter.AuthConverter;
import aurora.carevisionapiserver.global.auth.domain.RefreshToken;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.TokenResponse;
import aurora.carevisionapiserver.global.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Transactional
    public TokenResponse generate(String username) {
        String refreshToken = jwtUtil.createJwt("refresh", username, accessExpirationTime);
        String accessToken = jwtUtil.createJwt("access", username, refreshExpirationTime);
        saveRefreshToken(username, refreshToken, refreshExpirationTime);

        return AuthConverter.toTokenResponse(accessToken, refreshToken);
    }

    private void saveRefreshToken(
            String username, String refreshToken, long refreshExpirationTime) {
        Date expiration = new Date(System.currentTimeMillis() + refreshExpirationTime);
        RefreshToken newRefreshToken =
                RefreshToken.builder()
                        .username(username)
                        .refreshToken(refreshToken)
                        .expiration(expiration.toString())
                        .build();
        refreshTokenRepository.save(newRefreshToken);
    }

    public boolean isValidToken(String token) {
        return jwtUtil.isValidToken(token);
    }

    public Long extractUsername(String token) {
        return Long.valueOf(jwtUtil.getId(token));
    }
}
