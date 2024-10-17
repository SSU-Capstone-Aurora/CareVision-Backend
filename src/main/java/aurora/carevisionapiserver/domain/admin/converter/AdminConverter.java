package aurora.carevisionapiserver.domain.admin.converter;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.AdminDTO;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public class AdminConverter {

    public static Admin toAdmin(
            AdminDTO.AdminJoinDTO adminJoinDTO, String password, Hospital hospital) {
        return Admin.builder()
                .username(adminJoinDTO.getUsername())
                .password(password)
                .hospital(hospital)
                .build();
    }

    public static AdminDTO.AdminInfoDTO toAdminInfoDTO(Admin admin, Hospital hospital) {
        return AdminDTO.AdminInfoDTO.builder()
                .id(admin.getId())
                .hospital(hospital.getName())
                .department(hospital.getDepartment())
                .build();
    }
}
