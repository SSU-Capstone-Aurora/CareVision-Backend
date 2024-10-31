package aurora.carevisionapiserver.domain.admin.service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public interface AdminService {
    Admin createAdmin(AdminCreateRequest adminCreateRequest, Hospital hospital);

    boolean isUsernameDuplicated(String username);

    Admin getAdmin(Long adminId);

    Admin getAdmin(String username);
}
