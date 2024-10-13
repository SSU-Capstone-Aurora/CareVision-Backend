package aurora.carevisionapiserver.domain.hospital.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class HospitalDTO {
    @Getter
    @Builder
    public static class SearchHospitalDTO {
        private String name;
        private String address;
        private String ykiho;
    }

    @Getter
    @Builder
    public static class SearchHospitalDTOList {
        private List<SearchHospitalDTO> hospitals;
        private Long totalCount;
    }

    @Getter
    @Builder
    public static class SearchDepartmentDTO {
        private List<String> departments;
        private Long totalCount;
    }
}
