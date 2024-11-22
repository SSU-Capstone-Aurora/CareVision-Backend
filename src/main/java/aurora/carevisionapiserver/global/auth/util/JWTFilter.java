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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.global.auth.domain.CustomUserDetails;
import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // 토큰이 없다면 다음 필터로 넘긴다
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtUtil.parseToken(authHeader);

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, ErrorStatus.REFRESH_TOKEN_EXPIRED);
            return;
        }

        // token이 access인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            sendErrorResponse(response, ErrorStatus.INVALID_REFRESH_TOKEN);
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails;

        if (Role.NURSE.getRole().equals(role)) {
            customUserDetails = new CustomUserDetails(username, null, Role.NURSE, true);
        } else if (Role.ADMIN.getRole().equals(role)) {
            customUserDetails = new CustomUserDetails(username, null, Role.ADMIN, true);
        } else {
            sendErrorResponse(response, ErrorStatus.INVALID_ROLE);
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

    private void sendErrorResponse(HttpServletResponse response, ErrorStatus errorStatus)
            throws IOException {
        BaseResponse<Void> errorResponse =
                BaseResponse.onFailure(errorStatus.getCode(), errorStatus.getMessage(), null);

        response.setStatus(errorStatus.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
    }
}
