package aurora.carevisionapiserver.domain.hospital.converter;

import java.util.List;

import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchDepartmentDTO;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTOList;

public class HospitalConverter {
    public static SearchHospitalDTOList toSearchHospitalDTOList(List<SearchHospitalDTO> hospitals) {
        return SearchHospitalDTOList.builder()
                .hospitals(hospitals)
                .totalCount((long) hospitals.size())
                .build();
    }

    public static SearchDepartmentDTO toSearchDepartmentDTO(List<String> departments) {
        return SearchDepartmentDTO.builder()
                .departments(departments)
                .totalCount((long) departments.size())
                .build();
    }
}
