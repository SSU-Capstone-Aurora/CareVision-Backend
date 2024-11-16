package aurora.carevisionapiserver.global.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.secret}")
    private String serviceAccountJson;

    @PostConstruct
    public void init() throws IOException {
        FirebaseOptions options =
                FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials.fromStream(
                                        new ByteArrayInputStream(
                                                serviceAccountJson.getBytes(
                                                        StandardCharsets.UTF_8))))
                        .build();
        FirebaseApp.initializeApp(options);
    }
}
