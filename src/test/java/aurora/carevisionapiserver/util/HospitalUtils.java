package aurora.carevisionapiserver.util;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public class HospitalUtils {
    public static Hospital createHospital() {
        return Hospital.builder().id(1L).name("오로라 병원").department("성형외과").build();
    }
}
