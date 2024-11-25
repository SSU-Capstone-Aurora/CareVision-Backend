package aurora.carevisionapiserver.global.auth.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.TokenResponse;

public interface AuthService {
    Optional<Authentication> authenticate(String username, String password);

    TokenResponse generateTokens(String username);

    void validateUsername(String username);

    boolean isUsernameDuplicated(String username);

    void validateUsername(String username, Role role);

    TokenResponse handleReissue(String authorizationHeader);

    void logout(Long id, String refreshToken);
}
