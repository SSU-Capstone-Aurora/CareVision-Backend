package aurora.carevisionapiserver.domain.patient.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.service.BedService;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;
import aurora.carevisionapiserver.domain.patient.exception.PatientException;
import aurora.carevisionapiserver.domain.patient.repository.PatientEsRepository;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.domain.patient.service.PatientRegistrationService;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.PatientNameUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientEsRepository patientEsRepository;
    private final BedService bedService;
    private final AdminService adminService;
    private final NurseService nurseService;
    private final PatientRegistrationService patientRegistrationService;

    public Map<PatientDocument, Bed> searchPatient(String patientName) {
        List<PatientDocument> patients = patientEsRepository.searchByName(patientName);
        return getPatientDocumentBedMap(patients);
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

    @Override
    public Slice<Patient> getPatientSlice(Nurse nurse, Long lastIdx, int size) {
        return patientRepository.findPatientByNurse(nurse, lastIdx, size);
    }

    @Override
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
        Patient patient =
                patientRegistrationService.createPatient(
                        patientCreateRequest, nurse.getDepartment());
        connectNurseToPatient(patient, nurse);
    }

    @Override
    public void createAndConnectPatient(
            PatientCreateRequest patientCreateRequest,
            CameraSelectRequest cameraSelectRequest,
            Admin admin) {
        patientRegistrationService.createPatient(patientCreateRequest, admin.getDepartment());
    }

    @Override
    public PatientSearchListResponse searchUnlinkedPatients(String patientName) {
        List<PatientDocument> patients =
                patientEsRepository.searchByNameAndNurseIsNull(patientName);
        Map<PatientDocument, Bed> patientDocumentBedMap = getPatientDocumentBedMap(patients);
        return PatientConverter.toPatientSearchListResponse(patientDocumentBedMap);
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

    private void connectNurseToPatient(Patient patient, Nurse nurse) {
        nurseService.connectPatient(nurse, patient);
    }

    private Map<PatientDocument, Bed> getPatientDocumentBedMap(List<PatientDocument> patients) {
        Map<PatientDocument, Bed> patientInfo = new HashMap<>();

        for (PatientDocument patient : patients) {
            Long bedId = patient.getBedId();
            Bed bed = bedService.findById(bedId);
            patientInfo.put(patient, bed);
        }

        return patientInfo;
    }
}
