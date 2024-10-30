package aurora.carevisionapiserver.global.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refresh_token")
public class RefreshToken {
    @Id private String username;
    private String refreshToken;
    private String expiration;

    @Builder
    public RefreshToken(String username, String refreshToken, String expiration) {
        this.username = username;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
