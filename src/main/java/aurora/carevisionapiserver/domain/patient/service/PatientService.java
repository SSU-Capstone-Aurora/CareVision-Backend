package aurora.carevisionapiserver.domain.patient.service;

import java.util.List;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;

public interface PatientService {
    List<Patient> searchPatient(String patientName);

    Patient getPatient(Long patientId);

    List<Patient> getPatients(Nurse nurse);

    List<Patient> getPatients(Long adminId);

    void deletePatient(Long patientId);

    void createAndConnectPatient(
            PatientCreateRequest patientCreateRequest,
            CameraSelectRequest cameraSelectRequest,
            Nurse nurse);

    void createAndConnectPatient(
            PatientCreateRequest patientCreateRequest,
            CameraSelectRequest cameraSelectRequest,
            Admin admin);

    List<Patient> getUnlinkedPatients(Nurse nurse);
}
