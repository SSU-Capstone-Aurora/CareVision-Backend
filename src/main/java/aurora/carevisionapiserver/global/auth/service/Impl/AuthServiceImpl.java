package aurora.carevisionapiserver.global.auth.service.Impl;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.global.auth.domain.RefreshToken;
import aurora.carevisionapiserver.global.auth.repository.RefreshTokenRepository;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private SecretKey secretKey;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refresh_expiration_time}")
    private long refreshExpirationTime;

    @Value("${jwt.access_expiration_time}")
    private long accessExpirationTime;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

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
    public String generateAccessToken(String username, String role) {
        return jwtUtil.createJwt("access", username, role, accessExpirationTime);
    }

    @Override
    public String generateRefreshToken(String username, String role) {
        String refreshToken =
                jwtUtil.createJwt("refreshToken", username, role, refreshExpirationTime);
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
    @Transactional
    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    @Override
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        return auth.getAuthority();
    }
}
