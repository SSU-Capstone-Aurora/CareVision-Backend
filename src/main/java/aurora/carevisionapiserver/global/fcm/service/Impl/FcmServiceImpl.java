package aurora.carevisionapiserver.global.fcm.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aurora.carevisionapiserver.global.fcm.converter.ClientTokenConverter;
import aurora.carevisionapiserver.global.fcm.domain.ClientToken;
import aurora.carevisionapiserver.global.fcm.dto.FcmClientRequest;
import aurora.carevisionapiserver.global.fcm.repository.ClientTokenRepository;
import aurora.carevisionapiserver.global.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
    private final ClientTokenRepository clientTokenRepository;

    @Override
    @Transactional
    public void saveClientToken(FcmClientRequest fcmClientRequest) {
        ClientToken clientToken = ClientTokenConverter.toClientToken(fcmClientRequest);
        clientTokenRepository.save(clientToken);
    }
}
