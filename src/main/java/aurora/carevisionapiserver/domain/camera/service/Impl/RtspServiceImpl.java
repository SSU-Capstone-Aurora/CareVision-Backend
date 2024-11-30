package aurora.carevisionapiserver.domain.camera.service.Impl;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.camera.service.RtspService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.util.UriFormatter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RtspServiceImpl implements RtspService {
    private final UriFormatter uriFormatter;
    private final CameraService cameraService;

    @Override
    public Boolean requestThumbnailUrl(Patient patient) {
        String rtspUrl = cameraService.getStreamingUrl(patient);
        Long patientId = patient.getId();
        URI requestUrl = uriFormatter.requestThumbnailUrl(rtspUrl, patientId.toString());
        if (requestUrl == null) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
    }
}
