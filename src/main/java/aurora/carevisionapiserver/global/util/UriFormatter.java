package aurora.carevisionapiserver.global.util;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;

public class UriFormatter {
    @Value("${camera.thumbnail.url}")
    static String thumbnailUrl;

    public static URI getThumbnailUrl(String rtspUrl, String patientId) {
        String encodedRtspUrl = URLEncoder.encode(rtspUrl, StandardCharsets.UTF_8);
        try {
            return new URI(thumbnailUrl + "?url=" + encodedRtspUrl + "&patient_id=" + patientId);
        } catch (Exception e) {
            return null;
        }
    }
}
