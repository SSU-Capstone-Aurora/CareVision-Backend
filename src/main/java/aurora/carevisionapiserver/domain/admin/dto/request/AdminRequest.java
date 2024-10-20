package aurora.carevisionapiserver.domain.admin.dto.request;

import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import lombok.Builder;
import lombok.Getter;

public class AdminRequest {

    @Getter
    @Builder
    public static class AdminSignUpRequest {
        private AdminCreateRequest admin;
        private HospitalCreateRequest hospital;
    }

    @Getter
    @Builder
    public static class AdminCreateRequest {

        private String username;

        private String password;
    }
}