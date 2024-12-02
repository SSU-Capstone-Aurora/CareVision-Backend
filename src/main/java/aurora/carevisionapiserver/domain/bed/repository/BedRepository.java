package aurora.carevisionapiserver.domain.bed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.bed.domain.Bed;

public interface BedRepository extends JpaRepository<Bed, Long> {
    Optional<Bed> findByBedNumberAndInpatientWardNumberAndPatientRoomNumber(
            Long bedNumber, Long inpatientWardNumber, Long patientRoomNumber);
}
