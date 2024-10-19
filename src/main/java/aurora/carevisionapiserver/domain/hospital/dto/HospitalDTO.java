package aurora.carevisionapiserver.domain.hospital.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class HospitalDTO {
    @Getter
    @Builder
    public static class HospitalSearchResponse {
        private String name;
        private String address;
        private String ykiho;
    }

    @Getter
    @Builder
    public static class HospitalSearchListResponse {
        private List<HospitalSearchResponse> hospitals;
        private Long totalCount;
    }

    @Getter
    @Builder
    public static class DepartmentSearchResponse {
        private List<String> departments;
        private Long totalCount;
    }

    @Getter
    @Builder
    public static class HospitalCreateRequest {
        private String hospital;
        private String department;
    }

    @Getter
    @Builder
    public static class HospitalInfoResponse {
        private String name;
        private String department;
    }
}
