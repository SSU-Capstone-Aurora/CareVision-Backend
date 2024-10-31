package aurora.carevisionapiserver.global.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.auth.domain.CustomUserDetails;
import aurora.carevisionapiserver.global.auth.exception.AuthException;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final NurseRepository nurseRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            return new CustomUserDetails(
                    admin.getUsername(), admin.getPassword(), admin.getRole(), true);
        }

        Nurse nurse =
                nurseRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new AuthException(ErrorStatus.USERNAME_NOT_FOUND));

        return new CustomUserDetails(
                nurse.getUsername(), nurse.getPassword(), nurse.getRole(), nurse.isActivated());
    }
}