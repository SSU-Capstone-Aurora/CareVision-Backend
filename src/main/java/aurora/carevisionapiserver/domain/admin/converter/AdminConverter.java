package aurora.carevisionapiserver.domain.admin.converter;

import static aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminCreateRequest;
import static aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminInfoResponse;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public class AdminConverter {

    public static Admin toAdmin(
            AdminCreateRequest adminCreateRequest, String password, Hospital hospital) {
        return Admin.builder()
                .username(adminCreateRequest.getUsername())
                .password(password)
                .hospital(hospital)
                .build();
    }

    public static AdminInfoResponse toAdminInfoResponse(Admin admin) {
        return AdminInfoResponse.builder()
                .id(admin.getId())
                .hospital(admin.getHospital().getName())
                .department(admin.getHospital().getDepartment())
                .build();
    }
}
