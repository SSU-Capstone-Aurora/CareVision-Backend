package aurora.carevisionapiserver.global.infra.aws;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "X_HealthCheck 🏥", description = "서버 상태 체크 API")
public class HealthCheckController {
    @GetMapping("/health")
    public String healthCheck() {
        return "나는 건강합니다 💪";
    }
}
