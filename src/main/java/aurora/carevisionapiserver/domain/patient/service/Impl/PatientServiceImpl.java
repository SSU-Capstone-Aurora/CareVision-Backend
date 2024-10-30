package aurora.carevisionapiserver.domain.patient.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final AdminService adminService;

    @Override
    public List<Patient> searchPatient(String patientName) {
        List<Patient> patients = patientRepository.searchByName(patientName);
        if (patients.size() == 0) throw new PatientException(ErrorStatus.PATIENT_NOT_FOUND);
        return patients;
    }

    @Override
    public List<Patient> getPatients(Nurse nurse) {
        return patientRepository.findPatientByNurse(nurse);
    }

    @Override
    public List<Patient> getPatients(Long adminId) {
        Admin admin = adminService.getAdmin(adminId);

        return patientRepository.findPatientByAdmin(admin);
    }
}
