package aurora.carevisionapiserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.Impl.NurseServiceImpl;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.util.AdminUtils;
import aurora.carevisionapiserver.util.HospitalUtils;
import aurora.carevisionapiserver.util.NurseUtils;

@ExtendWith(MockitoExtension.class)
public class NurseServiceTest {
    @InjectMocks private NurseServiceImpl nurseService;
    @Mock private NurseRepository nurseRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private AuthService authService;

    @Test
    @DisplayName("간호사 회원가입에 성공한다.")
    void createNurseSuccess() {
        // Given
        NurseCreateRequest nurseCreateRequest =
                NurseCreateRequest.builder()
                        .username("nurse1")
                        .password("password123")
                        .name("오로라")
                        .build();

        Hospital hospital = Hospital.builder().id(1L).name("오로라 병원").build();

        String encryptedPassword = "encryptedPassword123";
        Nurse expectedNurse =
                NurseConverter.toNurse(nurseCreateRequest, encryptedPassword, hospital);

        // When
        when(bCryptPasswordEncoder.encode(nurseCreateRequest.getPassword()))
                .thenReturn(encryptedPassword);
        when(nurseRepository.save(any(Nurse.class))).thenReturn(expectedNurse);

        Nurse resultNurse = nurseService.createNurse(nurseCreateRequest, hospital);

        // Then
        assertEquals(expectedNurse.getUsername(), resultNurse.getUsername());
        assertEquals(encryptedPassword, resultNurse.getPassword());
        assertEquals(expectedNurse.getHospital(), resultNurse.getHospital());
    }

    @Test
    @DisplayName("아이디 중복 체크가 성공한다.")
    void isUsernameDuplicatedSuccess() {
        // Given
        String username = "nurse1";
        given(nurseRepository.existsByUsername(username)).willReturn(true);

        // When
        boolean result = authService.isUsernameDuplicated(username);

        // Then
        assertTrue(result);
        verify(nurseRepository).existsByUsername(username);
    }

    @Test
    @DisplayName("아이디 중복 체크가 실패한다.")
    void isUsernameDuplicatedFailure() {
        // Given
        String username = "nurse2";
        given(nurseRepository.existsByUsername(username)).willReturn(false);

        // When
        boolean result = authService.validateUsername(username);

        // Then
        assertFalse(result);
        verify(nurseRepository).existsByUsername(username);
    }

    @Test
    @DisplayName("간호사 찾기 성공한다.")
    void getNurseListSuccess() {
        // given
        Hospital hospital = HospitalUtils.createHospital();
        Admin admin = AdminUtils.createAdmin(hospital);

        List<Nurse> nurses =
                List.of(NurseUtils.createOtherActiveNurse(), NurseUtils.createActiveNurse());
        given(nurseRepository.findActiveNursesByAdmin(admin)).willReturn(nurses);

        // when
        List<Nurse> result = nurseService.getActiveNurses(admin);

        // then
        assertEquals(nurses.size(), result.size());
        assertEquals(nurses.get(0).getId(), result.get(0).getId());
        assertEquals(nurses.get(0).getName(), result.get(0).getName());
        assertEquals(nurses.get(1).getId(), result.get(1).getId());
        assertEquals(nurses.get(1).getName(), result.get(1).getName());
    }

    @Test
    @DisplayName("간호사가 존재하는지 조회한다.")
    void existsNurseTest() {
        // given
        Nurse nurse = NurseUtils.createActiveNurse();
        given(nurseRepository.findByIdAndIsActivatedTrue(nurse.getId()))
                .willReturn(Optional.of(nurse));

        // when
        Nurse result = nurseService.getActiveNurse(nurse.getId());

        // then
        assertNotNull(result);
        assertEquals(nurse.getId(), result.getId());
        assertEquals(nurse.getName(), result.getName());
    }

    @Test
    @DisplayName("간호사 검색에 성공한다.")
    void searchNurseSuccess() {
        // given
        String nurseName = "test";
        List<Nurse> nurses =
                List.of(NurseUtils.createActiveNurse(), NurseUtils.createOtherActiveNurse());
        given(nurseRepository.searchByName(nurseName)).willReturn(nurses);

        // when
        List<Nurse> result = nurseService.searchNurse(nurseName);

        // then
        assertEquals(nurses.size(), result.size());
        assertEquals(nurses.get(0).getName(), result.get(0).getName());
        assertEquals(nurses.get(0).getId(), result.get(0).getId());
        assertEquals(nurses.get(1).getName(), result.get(1).getName());
        assertEquals(nurses.get(1).getId(), result.get(1).getId());
    }
}
