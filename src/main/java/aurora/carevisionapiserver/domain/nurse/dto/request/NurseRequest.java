package aurora.carevisionapiserver.domain.nurse.dto.request;

import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalSelectRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NurseRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class NurseSignUpRequest {
        private NurseCreateRequest nurse;
        private HospitalSelectRequest hospital;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class NurseCreateRequest {
        private String username;
        private String password;
        private String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class NurseLoginRequest {
        private String username;
        private String password;
    }
}
