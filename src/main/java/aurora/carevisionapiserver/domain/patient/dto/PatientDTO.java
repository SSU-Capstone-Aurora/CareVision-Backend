package aurora.carevisionapiserver.domain.patient.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class PatientDTO {
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
}
