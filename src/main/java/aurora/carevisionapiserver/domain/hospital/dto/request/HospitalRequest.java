package aurora.carevisionapiserver.domain.hospital.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HospitalRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalCreateRequest {
        private String ykiho;
        private String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DepartmentCreateRequest {
        private String name;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalSelectRequest {
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DepartmentSelectRequest {
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalDepartmentRequest {
        HospitalSelectRequest hospital;
        DepartmentSelectRequest department;
    }
}
