package aurora.carevisionapiserver.domain.admin.service;

import static aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminJoinDTO;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public interface AdminService {
    Admin createAdmin(AdminJoinDTO adminJoinDTO, Hospital hospital);
}
