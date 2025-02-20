package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public interface CustomPatientRepository {
    List<Patient> findPatientByAdmin(Admin admin);

    List<Patient> findUnlinkedPatientsByNurse(Nurse nurse);

    Slice<Patient> findPatientByNurse(Nurse nurse, Long lastIdx, int size);
}
