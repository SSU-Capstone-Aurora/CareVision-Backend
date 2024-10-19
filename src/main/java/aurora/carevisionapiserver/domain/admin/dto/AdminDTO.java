package aurora.carevisionapiserver.domain.admin.dto;

import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.HospitalCreateRequest;
import lombok.Builder;
import lombok.Getter;

public class AdminDTO {

    @Getter
    @Builder
    public static class AdminSignUpRequest {
        private AdminCreateRequest adminCreateRequest;
        private HospitalCreateRequest hospitalCreateRequest;
    }

    @Getter
    @Builder
    public static class AdminCreateRequest {

        private String username;

        private String password;
    }

    @Getter
    @Builder
    public static class AdminInfoResponse {
        private Long id;

        private String hospital;

        private String department;
    }
}
