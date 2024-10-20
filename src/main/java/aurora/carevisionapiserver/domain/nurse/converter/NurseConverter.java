package aurora.carevisionapiserver.domain.nurse.converter;

import static aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseCreateRequest;
import static aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseInfoResponse;

import java.util.List;
import java.util.Optional;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewListResponse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewResponse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseProfileResponse;

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

    public static NurseInfoResponse toNurseInfoResponse(Nurse nurse) {
        return NurseInfoResponse.builder().id(nurse.getId()).name(nurse.getName()).build();
    }

    public static Nurse toNurse(
            NurseCreateRequest nurseCreateRequest, String password, Hospital hospital) {
        return Nurse.builder()
                .username(nurseCreateRequest.getUsername())
                .name(nurseCreateRequest.getName())
                .password(password)
                .hospital(hospital)
                .isActivated(false)
                .build();
    }
}
