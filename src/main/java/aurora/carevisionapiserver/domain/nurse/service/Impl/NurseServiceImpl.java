package aurora.carevisionapiserver.domain.nurse.service.Impl;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NurseServiceImpl implements NurseService {
    private final NurseRepository nurseRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public boolean existsByNurseId(Long nurseId) {
        return nurseRepository.existsById(nurseId);
    }

    @Override
    public Optional<Nurse> getNurse(Long nurseId) {
        return nurseRepository.findById(nurseId);
    }

    @Override
    public List<Nurse> getNurseList() {
        return nurseRepository.findAll(Sort.by(Sort.Direction.DESC, "registeredAt"));
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
}
