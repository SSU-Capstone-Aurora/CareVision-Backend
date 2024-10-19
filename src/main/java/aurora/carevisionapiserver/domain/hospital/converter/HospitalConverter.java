package aurora.carevisionapiserver.domain.hospital.converter;

import java.util.List;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.DepartmentSearchResponse;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalSearchListResponse;

public class HospitalConverter {
    public static HospitalSearchListResponse toHospitalSearchListResponse(
            List<HospitalResponse.HospitalSearchResponse> hospitals) {
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

    public static HospitalResponse.HospitalInfoResponse toHospitalInfoResponse(Admin admin) {
        return HospitalResponse.HospitalInfoResponse.builder()
                .name(admin.getHospital().getName())
                .department(admin.getHospital().getDepartment())
                .build();
    }

    public static Hospital toHospital(String name, String department) {
        return Hospital.builder().name(name).department(department).build();
    }
}
