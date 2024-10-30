package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.global.common.repository.SearchRepository;

public interface NurseRepository extends JpaRepository<Nurse, Long>, SearchRepository<Nurse> {
    boolean existsByUsername(String username);

    Optional<Nurse> findByUsername(String username);
}
