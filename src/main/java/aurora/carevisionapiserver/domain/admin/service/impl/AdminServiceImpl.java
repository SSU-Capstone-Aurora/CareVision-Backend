package aurora.carevisionapiserver.domain.admin.service.impl;

import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.converter.AdminConverter;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminSignUpRequest;
import aurora.carevisionapiserver.domain.admin.exception.AdminException;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.auth.service.AuthService;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AuthService authService;
    private final HospitalService hospitalService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public Admin signup(AdminSignUpRequest request) {
        validateUsername(request);

        Department department = createHospitalAndDepartment(request);
        return createAdmin(request.getAdmin(), department);
    }

    private void validateUsername(AdminSignUpRequest request) {
        String username = request.getAdmin().getUsername();
        authService.validateUsername(username);
    }

    private Department createHospitalAndDepartment(AdminSignUpRequest request) {
        Hospital hospital = hospitalService.createHospital(request.getHospital());
        return hospitalService.createDepartment(request.getDepartment(), hospital);
    }

    private Admin createAdmin(AdminCreateRequest request, Department department) {
        String encryptedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        Admin admin = AdminConverter.toAdmin(request, encryptedPassword, department);
        try {
            return adminRepository.save(admin);
        } catch (DataIntegrityViolationException e) {
            throw new AdminException(ErrorStatus.USERNAME_DUPLICATED);
        }
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
