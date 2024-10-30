package aurora.carevisionapiserver.domain.admin.converter;

import static aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import static aurora.carevisionapiserver.domain.admin.dto.response.AdminResponse.AdminInfoResponse;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.response.AdminResponse.AdminSignUpResponse;
import aurora.carevisionapiserver.domain.hospital.converter.HospitalConverter;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalInfoResponse;
import aurora.carevisionapiserver.global.auth.domain.Role;

public class AdminConverter {

    public static Admin toAdmin(
            AdminCreateRequest adminCreateRequest, String password, Hospital hospital) {
        return Admin.builder()
                .username(adminCreateRequest.getUsername())
                .password(password)
                .role(Role.ADMIN)
                .hospital(hospital)
                .build();
    }

    public static AdminSignUpResponse toAdminSignUpResponse(Admin admin) {
        AdminInfoResponse adminInfoResponse = toAdminInfoResponse(admin);
        HospitalInfoResponse hospitalInfoResponse = HospitalConverter.toHospitalInfoResponse(admin);

        return AdminSignUpResponse.builder()
                .admin(adminInfoResponse)
                .hospital(hospitalInfoResponse)
                .build();
    }

    private static AdminInfoResponse toAdminInfoResponse(Admin admin) {
        return AdminInfoResponse.builder().id(admin.getId()).build();
    }
}
