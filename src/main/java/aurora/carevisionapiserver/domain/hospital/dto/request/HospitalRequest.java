package aurora.carevisionapiserver.domain.hospital.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HospitalRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalCreateRequest {
        private String hospital;
        private String department;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalSelectRequest {
        private Long id;
    }
}
