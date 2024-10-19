package aurora.carevisionapiserver.domain.hospital.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.HospitalCreateRequest;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.HospitalSearchResponse;

public interface HospitalService {

    Hospital createHospital(HospitalCreateRequest createHospitalDTO);

    List<HospitalSearchResponse> searchHospital(String hospitalName) throws IOException;

    List<HospitalSearchResponse> parseHospitalInfo(StringBuilder hospitalInfo)
            throws JsonProcessingException;

    List<String> searchDepartment(String ykiho) throws IOException;

    List<String> parseDepartmentInfo(StringBuilder departmentInfo) throws IOException;
}
