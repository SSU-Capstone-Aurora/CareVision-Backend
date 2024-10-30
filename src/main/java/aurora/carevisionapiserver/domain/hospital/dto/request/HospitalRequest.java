package aurora.carevisionapiserver.domain.hospital.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class HospitalRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class HospitalCreateRequest {
        private String hospital;
        private String department;
    }

    @Getter
    @AllArgsConstructor
    public static class HospitalSelectRequest {
        private Long id;
    }
}
