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
    public static class HospitalListResponse<T> {
        private List<T> hospitals;
        private Long totalCount;
    }

    @Getter
    @Builder
    public static class DepartmentResponse {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class DepartmentListResponse {
        private List<String> departments;
        private Long totalCount;
    }

    @Getter
    @Builder
    public static class DepartmentListForNurseResponse {
        private List<DepartmentResponse> departments;
        private Long totalCount;
    }

    @Getter
    @Builder
    public static class HospitalInfoResponse {
        private String name;
        private String department;
    }

    @Getter
    @Builder
    public static class HospitalIdentifierResponse {
        private Long id;
        private String name;
    }
}
