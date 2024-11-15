package aurora.carevisionapiserver.domain.hospital.converter;

import static aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.*;

import java.util.List;
import java.util.stream.Collectors;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.DepartmentListResponse;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalInfoResponse;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalListResponse;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;

public class HospitalConverter {
    public static HospitalListResponse<HospitalSearchResponse> toHospitalSearchListResponse(
            List<HospitalSearchResponse> hospitals) {
        return HospitalListResponse.<HospitalSearchResponse>builder()
                .hospitals(hospitals)
                .totalCount((long) hospitals.size())
                .build();
    }

    public static HospitalListResponse<HospitalIdentifierResponse> toHospitalNameListResponse(
            List<Hospital> hospitals) {
        List<HospitalIdentifierResponse> hospitalIdentifierRespons =
                toHospitalNameResponseList(hospitals);
        return HospitalListResponse.<HospitalIdentifierResponse>builder()
                .hospitals(hospitalIdentifierRespons)
                .totalCount((long) hospitals.size())
                .build();
    }

    private static List<HospitalIdentifierResponse> toHospitalNameResponseList(
            List<Hospital> hospitals) {
        return hospitals.stream()
                .map(HospitalConverter::toHospitalIdentifierResponse)
                .collect(Collectors.toList());
    }

    private static HospitalIdentifierResponse toHospitalIdentifierResponse(Hospital hospital) {
        return HospitalIdentifierResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .build();
    }

    public static DepartmentListResponse toDepartmentListResponsse(List<String> departments) {
        return DepartmentListResponse.builder()
                .departments(departments)
                .totalCount((long) departments.size())
                .build();
    }

    public static HospitalInfoResponse toHospitalInfoResponse(Admin admin) {
        return HospitalInfoResponse.builder()
                .name(admin.getHospital().getName())
                .department(admin.getDepartment().getName())
                .build();
    }

    public static HospitalInfoResponse toHospitalInfoResponse(Nurse nurse) {
        return HospitalInfoResponse.builder()
                .name(nurse.getHospital().getName())
                .department(nurse.getDepartment().getName())
                .build();
    }

    public static Hospital toHospital(HospitalCreateRequest hospitalCreateRequest) {
        return Hospital.builder()
                .name(hospitalCreateRequest.getName())
                .ykiho(hospitalCreateRequest.getYkiho())
                .build();
    }

    public static Department toDepartment(String name, Hospital hospital) {
        return Department.builder().name(name).hospital(hospital).build();
    }
}
