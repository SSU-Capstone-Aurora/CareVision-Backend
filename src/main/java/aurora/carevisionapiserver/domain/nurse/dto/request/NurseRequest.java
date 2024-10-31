package aurora.carevisionapiserver.domain.nurse.dto.request;

import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalSelectRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NurseRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NurseSignUpRequest {
        private NurseCreateRequest nurse;
        private HospitalSelectRequest hospital;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NurseCreateRequest {
        private String username;
        private String password;
        private String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NurseLoginRequest {
        private String username;
        private String password;
    }
}
