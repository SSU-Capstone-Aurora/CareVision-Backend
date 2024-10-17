package aurora.carevisionapiserver.domain.admin.service.impl;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.converter.AdminConverter;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.AdminDTO;
import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Admin createAdmin(AdminDTO.AdminJoinDTO adminJoinDTO, Hospital hospital) {
        String encryptedPassword = bCryptPasswordEncoder.encode(adminJoinDTO.getPassword());
        Admin admin = AdminConverter.toAdmin(adminJoinDTO, encryptedPassword, hospital);
        return adminRepository.save(admin);
    }
}
