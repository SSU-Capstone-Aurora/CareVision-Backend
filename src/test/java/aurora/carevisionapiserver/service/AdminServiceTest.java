package aurora.carevisionapiserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminJoinDTO;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.admin.service.impl.AdminServiceImpl;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @InjectMocks private AdminServiceImpl adminService;
    @Mock private AdminRepository adminRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("관리자 회원가입에 성공한다.")
    void createAdminSuccess() {
        // Given
        AdminJoinDTO adminJoinDTO =
                AdminJoinDTO.builder()
                        .username("admin1")
                        .password("password123")
                        .hospital("오로라 병원")
                        .department("성형외과")
                        .build();
        Hospital hospital = Hospital.builder().id(1L).name("오로라 병원").department("성형외과").build();
        String encryptedPassword = "encryptedPassword123";
        Admin admin = AdminConverter.toAdmin(adminJoinDTO, encryptedPassword, hospital);

        // When
        when(bCryptPasswordEncoder.encode(adminJoinDTO.getPassword()))
                .thenReturn(encryptedPassword);

        when(adminRepository.save(any(Admin.class))).thenReturn(admin);
        Admin resultAdmin = adminService.createAdmin(adminJoinDTO, hospital);

        // Then
        assertEquals(admin.getUsername(), resultAdmin.getUsername());
        assertEquals(encryptedPassword, resultAdmin.getPassword());
        assertEquals(hospital, resultAdmin.getHospital());
    }
}
