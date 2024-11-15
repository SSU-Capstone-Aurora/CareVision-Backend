package aurora.carevisionapiserver.domain.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.hospital.domain.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {}
