package aurora.carevisionapiserver.domain.patient.converter;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;

public class PatientDocumentConverter {
    public static PatientDocument toPatientDocument(Patient patient) {
        return PatientDocument.builder()
                .name(patient.getName())
                .code(patient.getCode())
                .bedId(patient.getBed().getId())
                .patientId(patient.getId())
                .build();
    }
}
