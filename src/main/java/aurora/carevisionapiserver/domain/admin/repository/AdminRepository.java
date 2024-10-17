package aurora.carevisionapiserver.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aurora.carevisionapiserver.domain.admin.domain.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {}
