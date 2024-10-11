package aurora.carevisionapiserver.domain.nurse.service.Impl;

import java.util.Optional;

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
        System.out.println("nurseId:  " + nurseId);
        return nurseRepository.existsById(nurseId);
    }

    @Override
    public Optional<Nurse> getNurse(Long nurseId) {
        return nurseRepository.findById(nurseId);
    }
}
