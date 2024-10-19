package aurora.carevisionapiserver.domain.hospital.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class HospitalResponse {
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
    public static class HospitalInfoResponse {
        private String name;
        private String department;
    }
}
