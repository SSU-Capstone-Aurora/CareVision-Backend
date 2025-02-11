package aurora.carevisionapiserver.domain.admin.service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminSignUpRequest;

public interface AdminService {
    Admin signup(AdminSignUpRequest adminSignUpRequest);

    Admin getAdmin(Long adminId);

    Admin getAdmin(String username);
}
