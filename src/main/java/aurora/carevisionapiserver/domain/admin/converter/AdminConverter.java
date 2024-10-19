package aurora.carevisionapiserver.domain.admin.converter;

import static aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import static aurora.carevisionapiserver.domain.admin.dto.response.AdminResponse.AdminInfoResponse;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.response.AdminResponse.AdminSignUpResponse;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalInfoResponse;

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
        AdminInfoResponse adminInfoResponse = toAdminInfoResponse(admin);
        HospitalInfoResponse hospitalInfoResponse = toHospitalInfoResponse(admin);

        return AdminSignUpResponse.builder()
                .admin(adminInfoResponse)
                .hospital(hospitalInfoResponse)
                .build();
    }

    private static AdminInfoResponse toAdminInfoResponse(Admin admin) {
        return AdminInfoResponse.builder().id(admin.getId()).build();
    }

    private static HospitalInfoResponse toHospitalInfoResponse(Admin admin) {
        return HospitalInfoResponse.builder()
                .name(admin.getHospital().getName())
                .department(admin.getHospital().getDepartment())
                .build();
    }
}
