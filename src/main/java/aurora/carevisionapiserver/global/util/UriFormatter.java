package aurora.carevisionapiserver.global.util;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UriFormatter {
    @Value("${camera.thumbnail.url}")
    private String thumbnailUrl;

    public URI requestThumbnailUrl(String rtspUrl, String patientId) {
        String encodedRtspUrl = URLEncoder.encode(rtspUrl, StandardCharsets.UTF_8);
        try {
            return new URI(thumbnailUrl + "?url=" + encodedRtspUrl + "&patient_id=" + patientId);
        } catch (Exception e) {
            return null;
        }
    }

    public String getThumbnailUrl(String bucket, String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
    }
}
