package aurora.carevisionapiserver.global.auth.service.Impl;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.TokenResponse;
import aurora.carevisionapiserver.global.auth.exception.AuthException;
import aurora.carevisionapiserver.global.auth.repository.RefreshTokenRepository;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import aurora.carevisionapiserver.global.auth.util.RefreshTokenValidator;
import aurora.carevisionapiserver.global.auth.util.TokenGenerator;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AdminRepository adminRepository;
    private final NurseRepository nurseRepository;
    private final RefreshTokenValidator refreshTokenValidator;
    private final TokenGenerator tokenGenerator;

    @Override
    public Optional<Authentication> authenticate(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            return Optional.of(authenticationManager.authenticate(authToken));
        } catch (AuthenticationException e) {
            return Optional.empty();
        }
    }

    @Override
    public TokenResponse generateTokens(String username) {
        return tokenGenerator.generate(username);
    }

    @Override
    public void validateUsername(String username) {
        if (isUsernameDuplicated(username)) {
            throw new AuthException(ErrorStatus.USERNAME_DUPLICATED);
        }
    }

    @Override
    public boolean isUsernameDuplicated(String username) {
        boolean isAdminDuplicated = adminRepository.existsByUsername(username);
        boolean isNurseDuplicated = nurseRepository.existsByUsername(username);

        return isAdminDuplicated || isNurseDuplicated;
    }

    @Override
    public void validateUsername(String username, Role role) {
        if (role.equals(Role.ADMIN)) {
            adminRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_FOUND));
        } else if (role.equals(Role.NURSE)) {
            nurseRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_FOUND));
        }
    }

    @Override
    @Transactional
    public TokenResponse handleReissue(String refreshToken) {
        refreshTokenValidator.validateToken(refreshToken);
        refreshTokenValidator.validateTokenOwnerId(refreshToken);

        // 이전 refresh token 삭제
        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        String username = jwtUtil.getId(refreshToken);

        return tokenGenerator.generate(username);
    }

    @Override
    public void logout(Long id, String refreshToken) {
        refreshTokenValidator.validateToken(refreshToken);
        refreshTokenValidator.validateTokenOwnerId(refreshToken);

        // 이전 refresh token 삭제
        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        // TODO : 블랙 리스트 구현
    }
}
