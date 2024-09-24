package aurora.carevisionapiserver.domain.hospital.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Department {
    INTERNAL_MEDICINE("내과"),
    SURGERY("외과"),
    OBSTETRICS_AND_GYNECOLOGY("산부인과"),
    PEDIATRICS("소아청소년과"),
    PSYCHIATRY("정신건강의학과"),
    NEUROLOGY("신경과");

    private final String viewName;

    public static Department from(String value) {
        for (Department status : Department.values()) {
            if (status.getViewName().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(
                "Invalid format: "
                        + value
                        + ". Please use formats one of the following formats: '내과', '외과', etc.");
    }
}
