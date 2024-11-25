package aurora.carevisionapiserver.global.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

public class AuthResponse {
    @Getter
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;

        @Builder
        public TokenResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
