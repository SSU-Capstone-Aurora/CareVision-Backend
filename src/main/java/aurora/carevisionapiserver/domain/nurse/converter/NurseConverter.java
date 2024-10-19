package aurora.carevisionapiserver.domain.nurse.converter;

import java.util.List;
import java.util.Optional;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.NurseDTO.NursePreviewListResponse;
import aurora.carevisionapiserver.domain.nurse.dto.NurseDTO.NursePreviewResponse;
import aurora.carevisionapiserver.domain.nurse.dto.NurseDTO.NurseProfileResponse;

public class NurseConverter {
    public static NurseProfileResponse toNurseProfileResponse(Optional<Nurse> nurse) {
        Nurse tmpNurse = nurse.get();
        return NurseProfileResponse.builder()
                .name(tmpNurse.getName())
                .hospitalName(tmpNurse.getHospital().getName())
                .department(tmpNurse.getHospital().getDepartment().toString())
                .registeredAt(tmpNurse.getRegisteredAt().toLocalDate())
                .build();
    }

    public static NursePreviewResponse toNursePreviewResponse(Nurse nurse) {
        return NursePreviewResponse.builder().name(nurse.getName()).id(nurse.getUsername()).build();
    }

    public static NursePreviewListResponse toNursePreviewListResponse(List<Nurse> nurses) {
        return NursePreviewListResponse.builder()
                .nurseList(nurses.stream().map(NurseConverter::toNursePreviewResponse).toList())
                .count(nurses.size())
                .build();
    }
}
