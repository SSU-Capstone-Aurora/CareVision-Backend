package aurora.carevisionapiserver.domain.patient.converter;

import static aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchResponse;

import java.util.List;
import java.util.Map;

import aurora.carevisionapiserver.domain.bed.converter.BedConverter;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientNameResponse;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientProfileListResponse;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientProfileResponse;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;

public class PatientConverter {
    private static PatientSearchResponse toPatientSearchResponse(Patient patient) {
        return PatientSearchResponse.builder()
                .patientName(patient.getName())
                .inpatientWardNumber(patient.getBed().getInpatientWardNumber())
                .patientRoomNumber(patient.getBed().getPatientRoomNumber())
                .bedNumber(patient.getBed().getBedNumber())
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

    private static PatientSearchResponse toPatientSearchResponse(PatientDocument patient, Bed bed) {
        return PatientSearchResponse.builder()
                .patientName(patient.getName())
                .inpatientWardNumber(bed.getInpatientWardNumber())
                .patientRoomNumber(bed.getPatientRoomNumber())
                .bedNumber(bed.getBedNumber())
                .code(patient.getCode())
                .build();
    }

    public static PatientSearchListResponse toPatientSearchListResponse(
            Map<PatientDocument, Bed> patients) {
        return PatientSearchListResponse.builder()
                .patientList(
                        patients.entrySet().stream()
                                .map(
                                        entry ->
                                                toPatientSearchResponse(
                                                        entry.getKey(), entry.getValue()))
                                .toList())
                .count(patients.size())
                .build();
    }

    public static PatientProfileResponse toPatientProfileResponse(Patient patient) {
        Bed bed = patient.getBed();
        return PatientProfileResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .inpatientWardNumber(bed.getInpatientWardNumber())
                .patientRoomNumber(bed.getPatientRoomNumber())
                .bedNumber(bed.getBedNumber())
                .code(patient.getCode())
                .createdAt(patient.getCreatedAt().toLocalDate())
                .build();
    }

    public static PatientProfileListResponse toPatientProfileListResponse(List<Patient> patients) {
        return PatientProfileListResponse.builder()
                .patients(
                        patients.stream().map(PatientConverter::toPatientProfileResponse).toList())
                .count(patients.size())
                .build();
    }

    public static Patient toPatient(
            PatientCreateRequest patientCreateRequest, Bed bed, Department department) {
        if (bed == null) {
            bed = BedConverter.toBed(patientCreateRequest.getBed(), department);
        }
        Patient patient =
                Patient.builder()
                        .name(patientCreateRequest.getName())
                        .code(patientCreateRequest.getCode())
                        .bed(bed)
                        .department(department)
                        .build();
        bed.registerPatient(patient);
        return patient;
    }

    public static PatientNameResponse toPatientNameResponse(String name) {
        return PatientNameResponse.builder().name(name).build();
    }
}
