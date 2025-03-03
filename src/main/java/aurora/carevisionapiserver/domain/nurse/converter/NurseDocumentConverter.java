package aurora.carevisionapiserver.domain.nurse.converter;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;

public class NurseDocumentConverter {
    public static NurseDocument toNurseDocument(Nurse nurse) {
        return NurseDocument.builder().name(nurse.getName()).username(nurse.getUsername()).build();
    }
}
