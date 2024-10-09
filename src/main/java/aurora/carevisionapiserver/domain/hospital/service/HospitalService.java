package aurora.carevisionapiserver.domain.hospital.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;

public interface HospitalService {
    List<SearchHospitalDTO> searchHospital(String hospitalName) throws IOException;

    List<SearchHospitalDTO> parseHospitalInfo(StringBuilder hospitalInfo)
            throws JsonProcessingException;
}
