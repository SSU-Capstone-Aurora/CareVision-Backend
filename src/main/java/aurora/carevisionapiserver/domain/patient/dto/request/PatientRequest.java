package aurora.carevisionapiserver.domain.patient.dto.request;

import aurora.carevisionapiserver.domain.bed.dto.BedRequest.BedCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PatientRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatientCreateRequest {
        private String name;
        private String code;
        private BedCreateRequest bed;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatientSelectRequest {
        private Long patientId;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatientCodeRequest {
        private String code;
    }
}
