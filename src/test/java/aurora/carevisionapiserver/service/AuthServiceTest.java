package aurora.carevisionapiserver.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;

import aurora.carevisionapiserver.global.auth.service.Impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks private AuthServiceImpl authService;

    @Mock private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser
    @DisplayName("인증 성공 시 Optional에 Authentication 객체가 포함되어 반환된다.")
    void authenticateSuccess() {
        // Given
        String username = "kim1";
        String password = "password123";
        Authentication mockAuth = mock(Authentication.class); // Mocking Authentication object

        // Mock behavior: When authenticate is called with correct credentials, return mockAuth
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        // When
        Optional<Authentication> result = authService.authenticate(username, password);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockAuth, result.get());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("인증 실패 시 Optional.empty() 반환")
    void authenticateFailure() {
        // Given
        String username = "wrongUsername";
        String password = "wrongPassword";

        // authenticationManager가 AuthenticationException을 발생하도록 모의 설정
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("인증 실패") {});

        // When
        Optional<Authentication> result = authService.authenticate(username, password);

        // Then
        assertTrue(result.isEmpty(), "AuthenticationException 발생 시 Optional.empty()가 반환되어야 한다.");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
