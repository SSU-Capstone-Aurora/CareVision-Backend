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

import aurora.carevisionapiserver.global.auth.util.JWTFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTFilter jwtFilter;
    private final String[] allowedUrls = {
        "/api/admin/login",
        "/api/sign-up",
        "/api/check-username",
        "/api/hospitals/**",
        "/api/requests/**",
        "/api/admin/check-username",
        "/api/admin/sign-up",
        "/api/admin/departments",
        "/api/admin/hospitals",
        "/api/login",
        "/api/fcm/**",
        "/health",
        "/error",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/v3/api-docs/**",
    };

    private final String[] nurseUrls = {
        "/api/patients",
        "/api/profile",
        "/api/patients/**",
        "/api/streaming/**",
        "/api/reissue/**",
        "api/alarm",
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
                                .requestMatchers("/api/cameras/unlinked", "/api/patients/name")
                                .hasAnyRole("ADMIN", "NURSE")
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN")
                                .requestMatchers(nurseUrls)
                                .hasRole("NURSE")
                                .anyRequest()
                                .authenticated());

        // JWT 인증 필터 추가
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
