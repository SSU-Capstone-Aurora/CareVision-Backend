package aurora.carevisionapiserver.domain.nurse.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NurseServiceImpl implements NurseService {
    private final NurseRepository nurseRepository;

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
}
