package aurora.carevisionapiserver.domain.admin.converter;

import static aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminCreateRequest;
import static aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminInfoResponse;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminSignUpResponse;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.HospitalInfoResponse;

public class AdminConverter {

    public static Admin toAdmin(
            AdminCreateRequest adminCreateRequest, String password, Hospital hospital) {
        return Admin.builder()
                .username(adminCreateRequest.getUsername())
                .password(password)
                .hospital(hospital)
                .build();
    }

    public static AdminSignUpResponse toAdminSignUpResponse(Admin admin) {
        AdminInfoResponse adminInfoResponse = AdminInfoResponse.builder().id(admin.getId()).build();

        HospitalInfoResponse hospitalInfoResponse =
                HospitalInfoResponse.builder()
                        .name(admin.getHospital().getName())
                        .department(admin.getHospital().getDepartment())
                        .build();
        return AdminSignUpResponse.builder()
                .adminInfoResponse(adminInfoResponse)
                .hospitalInfoResponse(hospitalInfoResponse)
                .build();
    }
}
