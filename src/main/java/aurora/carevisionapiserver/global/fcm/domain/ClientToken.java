package aurora.carevisionapiserver.global.fcm.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "client_token")
public class ClientToken {
    @Id private String username;
    private String token;

    @Builder
    public ClientToken(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
