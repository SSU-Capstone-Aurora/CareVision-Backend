package aurora.carevisionapiserver.global.fcm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FcmRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FcmClientRequest {
        private ClientInfo clientInfo;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClientInfo {
        private String username;
        private String clientToken;
    }
}
