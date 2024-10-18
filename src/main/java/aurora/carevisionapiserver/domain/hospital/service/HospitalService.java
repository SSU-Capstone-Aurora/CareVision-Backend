package aurora.carevisionapiserver.domain.hospital.service;

import static aurora.carevisionapiserver.domain.admin.dto.AdminDTO.AdminJoinDTO;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;

public interface HospitalService {

    Hospital createHospital(AdminJoinDTO adminJoinDTO);

    List<SearchHospitalDTO> searchHospital(String hospitalName) throws IOException;

    List<SearchHospitalDTO> parseHospitalInfo(StringBuilder hospitalInfo)
            throws JsonProcessingException;

    List<String> searchDepartment(String ykiho) throws IOException;

    List<String> parseDepartmentInfo(StringBuilder departmentInfo) throws IOException;
}
