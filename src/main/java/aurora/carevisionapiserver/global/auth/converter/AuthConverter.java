package aurora.carevisionapiserver.global.auth.converter;

import aurora.carevisionapiserver.global.auth.dto.response.AuthResponse.LoginResponse;

public class AuthConverter {
    public static LoginResponse toLoginResponse(String accessToken, String refreshToken) {
        return LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
}
