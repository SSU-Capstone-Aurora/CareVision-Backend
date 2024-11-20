package aurora.carevisionapiserver.domain.hospital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByHospital(Hospital hospital);
}
