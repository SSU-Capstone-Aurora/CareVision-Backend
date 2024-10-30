package aurora.carevisionapiserver.global.auth.service;

import java.util.Optional;

import jakarta.servlet.http.Cookie;

import org.springframework.security.core.Authentication;

public interface AuthService {
    Optional<Authentication> authenticate(String username, String password);

    String generateAccessToken(String username, String role);

    String generateRefreshToken(String username, String role);

    void saveRefreshToken(String username, String refreshToken, long expiredMs);

    Cookie createRefreshTokenCookie(String refreshToken);

    String getCurrentUserRole();
}
