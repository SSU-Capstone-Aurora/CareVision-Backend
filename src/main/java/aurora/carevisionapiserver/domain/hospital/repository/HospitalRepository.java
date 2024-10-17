package aurora.carevisionapiserver.domain.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {}
