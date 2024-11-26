package aurora.carevisionapiserver.domain.patient.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class PatientResponse {
    @Builder
    @Getter
    public static class PatientSearchResponse {
        private String patientName;
        private Long inpatientWardNumber;
        private Long patientRoomNumber;
        private Long bedNumber;
        private String code;
    }

    @Builder
    @Getter
    public static class PatientSearchListResponse {
        private List<PatientSearchResponse> patientList;
        private int count;
    }

    @Builder
    @Getter
    public static class PatientProfileResponse {
        private Long id;
        private String name;
        private String code;
        private LocalDate createdAt;
        private Long inpatientWardNumber;
        private Long patientRoomNumber;
        private Long bedNumber;
    }

    @Builder
    @Getter
    public static class PatientProfileListResponse {
        private List<PatientProfileResponse> patients;
        private int count;
    }

    @Builder
    @Getter
    public static class PatientNameResponse {
        private String name;
    }
}
