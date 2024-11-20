package aurora.carevisionapiserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import aurora.carevisionapiserver.domain.admin.converter.AdminConverter;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.admin.service.impl.AdminServiceImpl;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.global.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @InjectMocks private AdminServiceImpl adminService;
    @Mock private AdminRepository adminRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock private AuthService authService;

    @Test
    @DisplayName("관리자 회원가입에 성공한다.")
    void createAdminSuccess() {
        // Given
        AdminCreateRequest adminCreateRequest =
                AdminCreateRequest.builder().username("admin1").password("password123").build();

        Hospital hospital = Hospital.builder().id(1L).name("오로라 병원").build();
        Department department = Department.builder().id(1L).name("정형외과").build();
        String encryptedPassword = "encryptedPassword123";
        Admin admin = AdminConverter.toAdmin(adminCreateRequest, encryptedPassword, department);

        // When
        when(bCryptPasswordEncoder.encode(adminCreateRequest.getPassword()))
                .thenReturn(encryptedPassword);

        when(adminRepository.save(any(Admin.class))).thenReturn(admin);
        Admin resultAdmin = adminService.createAdmin(adminCreateRequest, hospital, department);

        // Then
        assertEquals(admin.getUsername(), resultAdmin.getUsername());
        assertEquals(encryptedPassword, resultAdmin.getPassword());
        assertEquals(hospital, resultAdmin.getDepartment());
    }

    @Test
    @DisplayName("아이디 중복 체크가 성공한다.")
    void isUsernameDuplicatedSuccess() {
        // Given
        String username = "admin1";
        given(adminRepository.existsByUsername(username)).willReturn(true);

        // When
        boolean result = authService.isUsernameDuplicated(username);

        // Then
        assertTrue(result);
        verify(adminRepository).existsByUsername(username);
    }

    @Test
    @DisplayName("아이디 중복 체크가 실패한다.")
    void isUsernameDuplicatedFailure() {
        // Given
        String username = "admin2";
        given(adminRepository.existsByUsername(username)).willReturn(false);

        // When
        boolean result = authService.isUsernameDuplicated(username);

        // Then
        assertFalse(result);
        verify(adminRepository).existsByUsername(username);
    }
}
