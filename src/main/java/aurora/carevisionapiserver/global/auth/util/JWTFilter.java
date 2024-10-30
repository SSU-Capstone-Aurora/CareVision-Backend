package aurora.carevisionapiserver.global.auth.util;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.global.auth.domain.CustomUserDetails;
import aurora.carevisionapiserver.global.auth.domain.Role;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘긴다
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = parseAccessToken(authHeader);

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // token이 access인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails;

        if (Role.NURSE.getRole().equals(role)) {
            Nurse nurse =
                    Nurse.builder()
                            .username(username)
                            .password("temppassword")
                            .role(Role.NURSE)
                            .build();
            customUserDetails =
                    new CustomUserDetails(
                            username,
                            "temppassword",
                            Role.NURSE,
                            nurse.isActivated()); // Admin은 null
        } else if (Role.ADMIN.getRole().equals(role)) {
            Admin admin =
                    Admin.builder()
                            .username(username)
                            .password("temppassword")
                            .role(Role.ADMIN)
                            .build();
            customUserDetails =
                    new CustomUserDetails(
                            username, "temppassword", Role.ADMIN, true); // Nurse는 null
        } else {
            // 유효하지 않은 역할일 경우 처리
            PrintWriter writer = response.getWriter();
            writer.print("invalid role");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails, null, customUserDetails.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    public String parseAccessToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header;
    }
}
