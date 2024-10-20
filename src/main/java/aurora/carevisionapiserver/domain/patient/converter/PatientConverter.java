package aurora.carevisionapiserver.domain.patient.converter;

import static aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchResponse;

import java.util.List;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientProfileResponse;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientProfileListResponse;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;

public class PatientConverter {
    public static PatientSearchResponse toPatientSearchResponse(Patient patient) {
        return PatientSearchResponse.builder()
                .patientName(patient.getName())
                .inpatientWardNumber(
                        patient.getBed() != null ? patient.getBed().getInpatientWardNumber() : null)
                .patientRoomNumber(
                        patient.getBed() != null ? patient.getBed().getPatientRoomNumber() : null)
                .bedNumber(patient.getBed() != null ? patient.getBed().getBedNumber() : null)
                .code(patient.getCode())
                .build();
    }

    public static PatientSearchListResponse toPatientSearchListResponse(List<Patient> patients) {
        return PatientSearchListResponse.builder()
                .patientList(
                        patients.stream().map(PatientConverter::toPatientSearchResponse).toList())
                .count(patients.size())
                .build();
    }

    public static PatientProfileResponse toPatientProfileResponse(Patient patient) {
        return PatientProfileResponse.builder()
                .name(patient.getName())
                .inpatientWardNumber(
                        patient.getBed() != null ? patient.getBed().getInpatientWardNumber() : null)
                .patientRoomNumber(
                        patient.getBed() != null ? patient.getBed().getPatientRoomNumber() : null)
                .bedNumber(patient.getBed() != null ? patient.getBed().getBedNumber() : null)
                .code(patient.getCode())
                .createdAt(patient.getCreatedAt().toLocalDate())
                .build();
    }

    public static PatientProfileListResponse toPatientProfileListResponse(List<Patient> patients) {
        return PatientProfileListResponse.builder()
                .patients(patients.stream().map(PatientConverter::toPatientProfileResponse).toList())
                .count(patients.size())
                .build();
    }
}
