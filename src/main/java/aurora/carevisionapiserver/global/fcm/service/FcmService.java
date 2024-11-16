package aurora.carevisionapiserver.global.fcm.service;

import aurora.carevisionapiserver.global.fcm.dto.FcmClientRequest;

public interface FcmService {
    void saveClientToken(FcmClientRequest fcmClientRequest);
}
