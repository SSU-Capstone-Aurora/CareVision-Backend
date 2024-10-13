package aurora.carevisionapiserver.domain.nurse.converter;

import java.util.Optional;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
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
}
