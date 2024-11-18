package aurora.carevisionapiserver.domain.admin.dto.request;

import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.DepartmentCreateRequest;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminSignUpRequest {
        private AdminCreateRequest admin;
        private HospitalCreateRequest hospital;
        private DepartmentCreateRequest department;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminCreateRequest {

        private String username;

        private String password;
    }
}
