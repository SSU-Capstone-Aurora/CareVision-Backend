package aurora.carevisionapiserver.global.fcm.converter;

import aurora.carevisionapiserver.global.fcm.domain.ClientToken;
import aurora.carevisionapiserver.global.fcm.dto.FcmClientRequest;

public class ClientTokenConverter {
    public static ClientToken toClientToken(FcmClientRequest fcmClientRequest) {
        return ClientToken.builder()
                .username(fcmClientRequest.username())
                .token(fcmClientRequest.clientToken())
                .build();
    }
}
