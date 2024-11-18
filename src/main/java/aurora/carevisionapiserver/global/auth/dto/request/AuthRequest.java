package aurora.carevisionapiserver.global.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
