package aurora.carevisionapiserver.domain.bed.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import aurora.carevisionapiserver.domain.bed.domain.Bed;

public interface BedRepository extends JpaRepository<Bed, Long>, CustomBedRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Bed> findByBedNumberAndInpatientWardNumberAndPatientRoomNumber(
            Long bedNumber, Long inpatientWardNumber, Long patientRoomNumber);
}
