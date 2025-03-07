package aurora.carevisionapiserver.domain.patient.repository;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CustomPatientRepository {
    Slice<Patient> findPatientByAdmin(Admin admin, Long lastIdx, int size);

    Slice<Patient> findPatientByNurse(Nurse nurse, Long lastIdx, int size);
}
