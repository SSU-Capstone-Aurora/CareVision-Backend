package aurora.carevisionapiserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.auth.util.JWTFilter;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final NurseRepository nurseRepository;
    private final ObjectMapper objectMapper;

    private final String[] allowedUrls = {
        "/api/admin/login",
        "/api/sign-up",
        "/api/check-username",
        "/api/admin/check-username",
        "/api/admin/sign-up",
        "/api/login",
        "/health",
        "/error",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/v3/api-docs/**",
    };

    private final String[] nurseUrls = {
        "/api/patients", "/api/profile", "/api/reissue", "/api/patients/**"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);

        http.sessionManagement(
                sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers(
                headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        http.authorizeHttpRequests(
                authorize ->
                        authorize
                                .requestMatchers(allowedUrls)
                                .permitAll()
                                .requestMatchers("/api/reissue")
                                .hasAnyRole("ADMIN", "NURSE")
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN")
                                .requestMatchers(nurseUrls)
                                .hasRole("NURSE")
                                .anyRequest()
                                .authenticated());

        // JWT 인증 필터 추가
        http.addFilterBefore(
                new JWTFilter(jwtUtil, nurseRepository, objectMapper),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
