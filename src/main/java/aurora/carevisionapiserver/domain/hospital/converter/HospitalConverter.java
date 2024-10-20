package aurora.carevisionapiserver.domain.hospital.converter;

import static aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.*;

import java.util.List;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.DepartmentSearchResponse;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalInfoResponse;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalSearchListResponse;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;

public class HospitalConverter {
    public static HospitalSearchListResponse toHospitalSearchListResponse(
            List<HospitalSearchResponse> hospitals) {
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

    public static HospitalInfoResponse toHospitalInfoResponse(Admin admin) {
        return HospitalInfoResponse.builder()
                .name(admin.getHospital().getName())
                .department(admin.getHospital().getDepartment())
                .build();
    }

    public static HospitalInfoResponse toHospitalInfoResponse(Nurse nurse) {
        return HospitalInfoResponse.builder()
                .name(nurse.getHospital().getName())
                .department(nurse.getHospital().getDepartment())
                .build();
    }

    public static Hospital toHospital(String name, String department) {
        return Hospital.builder().name(name).department(department).build();
    }
}
