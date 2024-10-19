package aurora.carevisionapiserver.domain.admin.service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminCreateRequest;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public interface AdminService {
    Admin createAdmin(AdminCreateRequest adminCreateRequest, Hospital hospital);
}
