package aurora.carevisionapiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CarevisionApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarevisionApiServerApplication.class, args);
    }
}
