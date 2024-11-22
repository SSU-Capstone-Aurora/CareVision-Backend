package aurora.carevisionapiserver.global.auth.service.Impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.auth.converter.AuthConverter;
import aurora.carevisionapiserver.global.auth.domain.RefreshToken;
import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.LoginResponse;
import aurora.carevisionapiserver.global.auth.exception.AuthException;
import aurora.carevisionapiserver.global.auth.repository.RefreshTokenRepository;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AdminRepository adminRepository;
    private final NurseRepository nurseRepository;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Value("${jwt.access-expiration-time}")
    private long accessExpirationTime;

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
    @Transactional
    public String createAccessToken(String username, String role) {
        return jwtUtil.createJwt("access", username, role, accessExpirationTime);
    }

    @Override
    @Transactional
    public String createRefreshToken(String username, String role) {
        String refreshToken = jwtUtil.createJwt("refresh", username, role, refreshExpirationTime);
        saveRefreshToken(username, refreshToken, refreshExpirationTime);
        return refreshToken;
    }

    @Override
    @Transactional
    public void saveRefreshToken(String username, String refreshToken, long expiredMs) {
        Date expiration = new Date(System.currentTimeMillis() + expiredMs);
        RefreshToken newRefreshToken =
                RefreshToken.builder()
                        .username(username)
                        .refreshToken(refreshToken)
                        .expiration(expiration.toString())
                        .build();
        refreshTokenRepository.save(newRefreshToken);
    }

    @Override
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        return auth.getAuthority();
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
    public LoginResponse handleReissue(String role, String unparsedRefreshToken) {

        String refreshToken = jwtUtil.parseToken(unparsedRefreshToken);

        if (refreshToken == null) {
            throw new AuthException(ErrorStatus.REFRESH_TOKEN_NULL);
        }

        // 토큰이 만료되었는지 검사
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        // 토큰이 refresh인지 검사
        String category = jwtUtil.getCategory(refreshToken);

        System.out.println("category : " + category);

        if (!category.equals("refresh")) {
            throw new AuthException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        String username = jwtUtil.getUsername(refreshToken);

        try {
            refreshTokenRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new AuthException(ErrorStatus.INVALID_REFRESH_TOKEN));
        } catch (AuthException e) {
            throw new AuthException(ErrorStatus.INVALID_CREDENTIALS);
        }

        // 이전 refresh token 삭제
        refreshTokenRepository.deleteByUsername(username);

        String newRefreshToken =
                jwtUtil.createJwt("refresh", username, role, refreshExpirationTime);

        String newAccessToken = jwtUtil.createJwt("access", username, role, refreshExpirationTime);

        // refresh token 업데이트
        addRefreshToken(username, newRefreshToken, refreshExpirationTime);

        return AuthConverter.toLoginResponse(newAccessToken, newRefreshToken);
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
