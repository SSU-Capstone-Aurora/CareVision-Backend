package aurora.carevisionapiserver.domain.nurse.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

public class NurseDTO {
    @Getter
    @Builder
    public static class NurseProfileDTO {
        private String name;
        private LocalDate registeredAt;
        private String hospitalName;
        private String department;
    }
}
