package aurora.carevisionapiserver.domain.patient.service.Impl;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.service.BedService;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.PatientNameUtil;
import aurora.carevisionapiserver.global.util.PatientValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final BedService bedService;
    private final AdminService adminService;
    private final NurseService nurseService;
    private final PatientValidator patientValidator;

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

    public Patient getPatientsByPatientId(String patientCode) {
        Patient patient = patientRepository.findPatientByCode(patientCode);
        if (patient == null) throw new PatientException(ErrorStatus.PATIENT_NOT_FOUND);

        return patientRepository.findPatientByCode(patientCode);
    }

    @Override
    @Transactional
    public void deletePatient(Long patientId) {
        Patient patient = getPatient(patientId);
        patientRepository.delete(patient);
    }

    @Override
    @Transactional
    public void createAndConnectPatient(
            PatientCreateRequest patientCreateRequest,
            CameraSelectRequest cameraSelectRequest,
            Nurse nurse) {
        Patient patient = createPatient(patientCreateRequest, nurse.getDepartment());
        connectNurseToPatient(patient, nurse);
    }

    @Override
    @Transactional
    public void createAndConnectPatient(
            PatientCreateRequest patientCreateRequest,
            CameraSelectRequest cameraSelectRequest,
            Admin admin) {
        createPatient(patientCreateRequest, admin.getDepartment());
    }

    private void connectNurseToPatient(Patient patient, Nurse nurse) {
        nurseService.connectPatient(nurse, patient);
    }

    private Patient createPatient(
            PatientCreateRequest patientCreateRequest, Department department) {
        patientValidator.validatePatientCode(patientCreateRequest.getCode());

        Bed bed = bedService.findBed(patientCreateRequest.getBed());
        Patient patient = PatientConverter.toPatient(patientCreateRequest, bed, department);
        return patientRepository.save(patient);
    }

    @Override
    public List<Patient> getUnlinkedPatients(Nurse nurse) {
        return patientRepository.findUnlinkedPatientsByNurse(nurse);
    }

    @Override
    public String getPatientNameByCode(String patientCode) {
        return PatientNameUtil.generateRandomName(patientCode);
    }

    public Patient getPatient(Long patientId) {
        return patientRepository
                .findById(patientId)
                .orElseThrow(() -> new PatientException(ErrorStatus.PATIENT_NOT_FOUND));
    }
}
