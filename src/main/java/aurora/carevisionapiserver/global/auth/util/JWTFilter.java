package aurora.carevisionapiserver.global.auth.util;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.auth.domain.CustomUserDetailsAdmin;
import aurora.carevisionapiserver.global.auth.domain.CustomUserDetailsNurse;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JWTUtil jwtUtil;
    private final NurseRepository nurseRepository;
    private final AdminRepository adminRepository;

    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String parsedToken = authorizationHeader.substring(7);

            if (jwtUtil.isValidToken(parsedToken)) {
                String username = jwtUtil.getId(parsedToken);

                UserDetails userDetails = null;
                if (nurseRepository.existsByUsername(username)) {
                    Optional<Nurse> nurse = nurseRepository.findByUsername(username);
                    userDetails = new CustomUserDetailsNurse(nurse.get());
                }
                if (adminRepository.existsByUsername(username)) {
                    Optional<Admin> admin = adminRepository.findByUsername(username);
                    userDetails = new CustomUserDetailsAdmin(admin.get());
                }
                if (userDetails == null) {
                    throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext()
                        .setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                throw new GeneralException(ErrorStatus.AUTH_INVALID_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }
}
