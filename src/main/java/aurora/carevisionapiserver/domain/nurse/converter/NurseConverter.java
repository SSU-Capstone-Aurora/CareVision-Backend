package aurora.carevisionapiserver.domain.nurse.converter;

import java.util.List;
import java.util.Optional;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.NurseDTO.NursePreviewDTO;
import aurora.carevisionapiserver.domain.nurse.dto.NurseDTO.NursePreviewDTOList;
import aurora.carevisionapiserver.domain.nurse.dto.NurseDTO.NurseProfileDTO;

public class NurseConverter {
    public static NurseProfileDTO toNurseProfileDTO(Optional<Nurse> nurse) {
        Nurse tmpNurse = nurse.get();
        return NurseProfileDTO.builder()
                .name(tmpNurse.getName())
                .hospitalName(tmpNurse.getHospital().getName())
                .department(tmpNurse.getHospital().getDepartment().toString())
                .registeredAt(tmpNurse.getRegisteredAt().toLocalDate())
                .build();
    }

    public static NursePreviewDTO toNursePreviewDTO(Nurse nurse) {
        return NursePreviewDTO.builder().name(nurse.getName()).id(nurse.getUsername()).build();
    }

    public static NursePreviewDTOList toNursePreviewDTOList(List<Nurse> nurses) {
        return NursePreviewDTOList.builder()
                .nurseList(nurses.stream().map(NurseConverter::toNursePreviewDTO).toList())
                .count(nurses.size())
                .build();
    }
}
