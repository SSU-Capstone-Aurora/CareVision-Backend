package aurora.carevisionapiserver.global.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

public class AuthResponse {
    @Getter
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;

        @Builder
        public LoginResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
