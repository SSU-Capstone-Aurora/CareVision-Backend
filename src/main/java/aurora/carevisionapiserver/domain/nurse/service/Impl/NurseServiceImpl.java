package aurora.carevisionapiserver.domain.nurse.service.Impl;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.exception.NurseException;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NurseServiceImpl implements NurseService {
    private final NurseRepository nurseRepository;
    private final AdminService adminService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public boolean existsByNurseId(Long nurseId) {
        return nurseRepository.existsById(nurseId);
    }

    @Override
    public Nurse getNurse(String username) {
        return nurseRepository
                .findByUsername(username)
                .orElseThrow(() -> new NurseException(ErrorStatus.NURSE_NOT_FOUND));
    }

    @Override
    public Nurse getActiveNurse(Long nurseId) {
        return nurseRepository
                .findByIdAndIsActivatedTrue(nurseId)
                .orElseThrow(() -> new NurseException(ErrorStatus.NURSE_NOT_FOUND));
    }

    @Override
    public Nurse getInActiveNurse(Long nurseId) {
        return nurseRepository
                .findByIdAndIsActivatedFalse(nurseId)
                .orElseThrow(() -> new NurseException(ErrorStatus.NURSE_NOT_FOUND));
    }

    @Override
    public List<Nurse> getActiveNurses(Admin admin) {
        return nurseRepository.findActiveNursesByAdmin(admin);
    }

    @Override
    public List<Nurse> getInActiveNurses(Admin admin) {
        return nurseRepository.findInActiveNursesByAdmin(admin);
    }

    @Override
    public List<Nurse> searchNurse(String nurseName) {
        return nurseRepository.searchByName(nurseName);
    }

    @Override
    @Transactional
    public Nurse createNurse(NurseCreateRequest nurseCreateRequest, Hospital hospital) {
        String encryptedPassword = bCryptPasswordEncoder.encode(nurseCreateRequest.getPassword());
        Nurse nurse = NurseConverter.toNurse(nurseCreateRequest, encryptedPassword, hospital);
        return nurseRepository.save(nurse);
    }

    @Override
    public boolean isUsernameDuplicated(String username) {
        return nurseRepository.existsByUsername(username);
    }

    @Override
    public void activateNurse(Admin admin, Long nurseId) {
        Nurse nurse = getInActiveNurse(nurseId);
        nurse.activateNurse();
        nurseRepository.save(nurse);
    }

    @Override
    public void deleteNurse(Admin admin, Long nurseId) {
        Nurse nurse = getInActiveNurse(nurseId);
        nurseRepository.delete(nurse);
    }
}
