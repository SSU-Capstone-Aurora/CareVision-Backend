package aurora.carevisionapiserver.domain.admin.dto.response;

import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalInfoResponse;
import lombok.Builder;
import lombok.Getter;

public class AdminResponse {
    @Getter
    @Builder
    public static class AdminSignUpResponse {
        private AdminInfoResponse admin;

        private HospitalInfoResponse hospital;
    }

    @Getter
    @Builder
    public static class AdminInfoResponse {
        private Long id;
    }

    @Getter
    public static class AdminLoginResponse {
        private String accessToken;

        @Builder
        public AdminLoginResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
