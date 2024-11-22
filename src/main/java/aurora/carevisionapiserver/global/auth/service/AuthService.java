package aurora.carevisionapiserver.global.auth.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.LoginResponse;

public interface AuthService {
    Optional<Authentication> authenticate(String username, String password);

    String createAccessToken(String username, String role);

    String createRefreshToken(String username, String role);

    void saveRefreshToken(String username, String refreshToken, long expiredMs);

    String getCurrentUserRole();

    void validateUsername(String username);

    boolean isUsernameDuplicated(String username);

    void validateUsername(String username, Role role);

    LoginResponse handleReissue(String role, String refreshToken);
}
