package aurora.carevisionapiserver.global.config;

import java.io.FileInputStream;
import java.io.IOException;

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
        FileInputStream serviceAccount = new FileInputStream(serviceAccountJson);

        FirebaseOptions options =
                FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
