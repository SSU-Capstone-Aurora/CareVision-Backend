package aurora.carevisionapiserver.domain.nurse.dto.request;

import java.util.List;

import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.DepartmentSelectRequest;
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
        private DepartmentSelectRequest department;
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
    public static class NurseAcceptanceRetryRequest {
        private String username;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NurseRegisterRequestListResponse {
        private int requestCount;
        private List<NurseRegisterRequestInfoResponse> requests;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NurseRegisterRequestInfoResponse {
        private Long nurseId;
        private String name;
        private String username;
        private String requestTime;
    }

    public record NurseRegisterRequestCountResponse(long count) {
        public static NurseRegisterRequestCountResponse from(long count) {
            return new NurseRegisterRequestCountResponse(count);
        }
    }
}
