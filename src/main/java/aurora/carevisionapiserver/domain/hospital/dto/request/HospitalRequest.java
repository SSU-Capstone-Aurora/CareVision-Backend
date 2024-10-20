package aurora.carevisionapiserver.domain.hospital.dto.request;

import lombok.Builder;
import lombok.Getter;

public class HospitalRequest {
    @Getter
    @Builder
    public static class HospitalCreateRequest {
        private String hospital;
        private String department;
    }

    @Getter
    public static class HospitalSelectRequest {
        private Long id;
    }
}
