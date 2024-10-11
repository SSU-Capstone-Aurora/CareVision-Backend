package aurora.carevisionapiserver.domain.patient.converter;

import java.util.List;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.PatientDTO.SearchPatientDTO;
import aurora.carevisionapiserver.domain.patient.dto.PatientDTO.SearchPatientDTOList;

public class PatientConverter {
    public static SearchPatientDTO toSearchPatientDTO(Patient patient) {
        return SearchPatientDTO.builder()
                .patientName(patient.getName())
                .inpatientWardNumber(
                        patient.getBed() != null ? patient.getBed().getInpatientWardNumber() : null)
                .patientRoomNumber(
                        patient.getBed() != null ? patient.getBed().getPatientRoomNumber() : null)
                .bedNumber(patient.getBed() != null ? patient.getBed().getBedNumber() : null)
                .code(patient.getCode())
                .build();
    }

    public static SearchPatientDTOList toSearchPatientDTOList(List<Patient> patients) {
        return SearchPatientDTOList.builder()
                .patientList(patients.stream().map(PatientConverter::toSearchPatientDTO).toList())
                .count(patients.size())
                .build();
    }
}
