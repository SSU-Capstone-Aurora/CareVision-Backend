package aurora.carevisionapiserver.domain.hospital.converter;

import java.util.List;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.DepartmentSearchResponse;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.HospitalSearchListResponse;

public class HospitalConverter {
    public static HospitalSearchListResponse toHospitalSearchListResponse(
            List<HospitalDTO.HospitalSearchResponse> hospitals) {
        return HospitalSearchListResponse.builder()
                .hospitals(hospitals)
                .totalCount((long) hospitals.size())
                .build();
    }

    public static DepartmentSearchResponse toDepartmentSearchResponsse(List<String> departments) {
        return DepartmentSearchResponse.builder()
                .departments(departments)
                .totalCount((long) departments.size())
                .build();
    }

    public static Hospital toHospital(String name, String department) {
        return Hospital.builder().name(name).department(department).build();
    }
}
