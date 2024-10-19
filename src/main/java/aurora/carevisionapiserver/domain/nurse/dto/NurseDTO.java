package aurora.carevisionapiserver.domain.nurse.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class NurseDTO {
    @Getter
    @Builder
    public static class NurseProfileResponse {
        private String name;
        private LocalDate registeredAt;
        private String hospitalName;
        private String department;
    }

    @Getter
    @Builder
    public static class NursePreviewResponse {
        private String name;
        private String id;
    }

    @Builder
    @Getter
    public static class NursePreviewListResponse {
        private List<NursePreviewResponse> nurseList;
        private int count;
    }
}
