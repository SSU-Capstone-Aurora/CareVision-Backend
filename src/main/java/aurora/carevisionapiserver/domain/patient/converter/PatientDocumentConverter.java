package aurora.carevisionapiserver.domain.patient.converter;

import java.util.List;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;

public class PatientDocumentConverter {
    public static List<PatientDocument> toPatientDocumentList(List<Patient> patients) {
        return patients.stream().map(PatientDocumentConverter::toPatientDocument).toList();
    }

    public static PatientDocument toPatientDocument(Patient patient) {
        return PatientDocument.builder()
                .name(patient.getName())
                .code(patient.getCode())
                .bedId(patient.getBed().getId())
                .patientId(patient.getId())
                .nurseId(patient.getNurse() != null ? patient.getNurse().getId() : -1L)
                .build();
    }
}
