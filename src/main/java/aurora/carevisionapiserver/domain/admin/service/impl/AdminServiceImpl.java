package aurora.carevisionapiserver.domain.admin.service.impl;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.converter.AdminConverter;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.admin.exception.AdminException;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public Admin createAdmin(AdminCreateRequest adminCreateRequest, Department department) {

        String encryptedPassword = bCryptPasswordEncoder.encode(adminCreateRequest.getPassword());
        Admin admin = AdminConverter.toAdmin(adminCreateRequest, encryptedPassword, department);
        return adminRepository.save(admin);
    }

    @Override
    public Admin getAdmin(Long adminId) {
        return adminRepository
                .findById(adminId)
                .orElseThrow(() -> new AdminException(ErrorStatus.ADMIN_NOT_FOUND));
    }

    @Override
    public Admin getAdmin(String username) {
        return adminRepository
                .findByUsername(username)
                .orElseThrow(() -> new AdminException(ErrorStatus.ADMIN_NOT_FOUND));
    }
}
