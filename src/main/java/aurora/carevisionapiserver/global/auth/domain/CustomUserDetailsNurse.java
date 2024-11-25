package aurora.carevisionapiserver.global.auth.domain;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetailsNurse implements UserDetails {
    private final Nurse nurse;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + nurse.getRole().getRole());
    }

    @Override
    public String getPassword() {
        return nurse.getPassword();
    }

    @Override
    public String getUsername() {
        return nurse.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
