package aurora.carevisionapiserver.domain.admin.dto.request;

import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AdminRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AdminSignUpRequest {
        private AdminCreateRequest admin;
        private HospitalCreateRequest hospital;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AdminCreateRequest {

        private String username;

        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AdminLoginRequest {
        private String username;
        private String password;
    }
}
