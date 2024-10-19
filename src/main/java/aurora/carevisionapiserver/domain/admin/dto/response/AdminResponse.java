package aurora.carevisionapiserver.domain.admin.dto.response;

import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalInfoResponse;
import lombok.Builder;
import lombok.Getter;

public class AdminResponse {
    @Getter
    @Builder
    public static class AdminSignUpResponse {
        private AdminInfoResponse adminInfoResponse;

        private HospitalInfoResponse hospitalInfoResponse;
    }

    @Getter
    @Builder
    public static class AdminInfoResponse {
        private Long id;
    }
}
