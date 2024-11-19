package aurora.carevisionapiserver.domain.admin.service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.hospital.domain.Department;

public interface AdminService {
    Admin createAdmin(AdminCreateRequest adminCreateRequest, Department department);

    Admin getAdmin(Long adminId);

    Admin getAdmin(String username);
}
