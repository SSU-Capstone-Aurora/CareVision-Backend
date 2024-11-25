package aurora.carevisionapiserver.global.auth.converter;

import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.TokenResponse;

public class AuthConverter {
    public static TokenResponse toTokenResponse(String accessToken, String refreshToken) {
        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
}
