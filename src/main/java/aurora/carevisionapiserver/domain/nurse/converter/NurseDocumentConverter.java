package aurora.carevisionapiserver.domain.nurse.converter;

import java.util.List;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;

public class NurseDocumentConverter {
    public static List<NurseDocument> toNurseDocumentList(List<Nurse> nurses) {
        return nurses.stream().map(NurseDocumentConverter::toNurseDocument).toList();
    }

    public static NurseDocument toNurseDocument(Nurse nurse) {
        return NurseDocument.builder().name(nurse.getName()).username(nurse.getUsername()).build();
    }
}
