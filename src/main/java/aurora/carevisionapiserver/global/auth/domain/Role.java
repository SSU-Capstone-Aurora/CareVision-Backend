package aurora.carevisionapiserver.global.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    NURSE("NURSE"),
    ADMIN("ADMIN");

    private final String role;
}
