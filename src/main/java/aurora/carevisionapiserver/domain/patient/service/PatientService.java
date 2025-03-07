package aurora.carevisionapiserver.domain.patient.service;

import java.util.Map;

import org.springframework.data.domain.Slice;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;
import aurora.carevisionapiserver.global.common.dto.request.PageRequest;

public interface PatientService {
    Map<PatientDocument, Bed> searchPatient(String patientName);

    Patient getPatient(Long patientId);

    Slice<Patient> getPatients(Admin admin, PageRequest request);

    Slice<Patient> getPatientSlice(Nurse nurse, PageRequest request);

    void deletePatient(Long patientId);

    void createAndConnectPatient(
            PatientCreateRequest patientCreateRequest,
            CameraSelectRequest cameraSelectRequest,
            Nurse nurse);

    void createAndConnectPatient(
            PatientCreateRequest patientCreateRequest,
            CameraSelectRequest cameraSelectRequest,
            Admin admin);

    PatientSearchListResponse searchUnlinkedPatients(String patientName);

    String getPatientNameByCode(String patientCode);
}
