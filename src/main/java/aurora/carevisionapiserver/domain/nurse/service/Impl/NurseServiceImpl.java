package aurora.carevisionapiserver.domain.nurse.service.Impl;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.exception.NurseException;
import aurora.carevisionapiserver.domain.nurse.repository.NurseEsRepository;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NurseServiceImpl implements NurseService {
    private final NurseRepository nurseRepository;
    private final NurseEsRepository nurseEsRepository;
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
    public Nurse getInactiveNurse(Long nurseId) {
        return nurseRepository
                .findByIdAndIsActivatedFalse(nurseId)
                .orElseThrow(() -> new NurseException(ErrorStatus.NURSE_NOT_FOUND));
    }

    @Override
    public Nurse getInactiveNurse(String username) {
        return nurseRepository
                .findByUsernameAndIsActivatedFalse(username)
                .orElseThrow(() -> new NurseException(ErrorStatus.NURSE_NOT_FOUND));
    }

    @Override
    public List<Nurse> getActiveNurses(Admin admin) {
        return nurseRepository.findActiveNursesByAdmin(admin);
    }

    @Override
    public List<Nurse> getInactiveNurses(Admin admin) {
        return nurseRepository.findInactiveNursesByAdmin(admin);
    }

    @Override
    public List<NurseDocument> searchNurse(String nurseName) {
        return nurseEsRepository.findAllByName(nurseName);
    }

    @Override
    @Transactional
    public Nurse createNurse(NurseCreateRequest nurseCreateRequest, Department department) {
        String encryptedPassword = bCryptPasswordEncoder.encode(nurseCreateRequest.getPassword());
        Nurse nurse = NurseConverter.toNurse(nurseCreateRequest, encryptedPassword, department);
        return nurseRepository.save(nurse);
    }

    @Override
    @Transactional
    public void activateNurse(Long nurseId) {
        Nurse nurse = getInactiveNurse(nurseId);
        nurse.activateNurse();
        nurseRepository.save(nurse);
        nurseEsRepository.save(NurseConverter.toNurseDocument(nurse));
    }

    @Override
    @Transactional
    public void deleteInactiveNurse(Long nurseId) {
        Nurse nurse = getInactiveNurse(nurseId);
        nurseRepository.delete(nurse);
    }

    @Override
    public long getNurseRegisterRequestCount(Admin admin) {
        return nurseRepository.countInactiveNursesByAdmin(admin);
    }

    @Override
    @Transactional
    public void deleteActiveNurse(Long nurseId) {
        Nurse nurse = getActiveNurse(nurseId);
        nurseRepository.delete(nurse);
    }

    @Override
    @Transactional
    public void connectPatient(Nurse nurse, Patient patient) {
        patient.registerNurse(nurse);
        nurse.getPatients().add(patient);
    }

    @Override
    @Transactional
    public void retryAcceptanceRequest(String username) {
        Nurse nurse = getInactiveNurse(username);
        nurse.updateRequestedAt();
    }
}
