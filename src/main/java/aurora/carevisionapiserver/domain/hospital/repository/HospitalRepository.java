package aurora.carevisionapiserver.domain.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {}
