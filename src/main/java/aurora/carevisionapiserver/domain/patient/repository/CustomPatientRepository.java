package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CustomPatientRepository {
    List<Patient> findPatientByAdmin(Admin admin);
}
