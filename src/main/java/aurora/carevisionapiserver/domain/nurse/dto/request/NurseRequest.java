package aurora.carevisionapiserver.domain.nurse.dto.request;

import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalSelectRequest;
import lombok.Builder;
import lombok.Getter;

public class NurseRequest {

    @Getter
    @Builder
    public static class NurseSignUpRequest {
        private NurseCreateRequest nurse;
        private HospitalSelectRequest hospital;
    }

    @Getter
    @Builder
    public static class NurseCreateRequest {
        private String username;
        private String password;
        private String name;
    }
}
