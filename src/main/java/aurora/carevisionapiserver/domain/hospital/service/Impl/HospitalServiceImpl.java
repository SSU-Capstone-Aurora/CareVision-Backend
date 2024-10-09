package aurora.carevisionapiserver.domain.hospital.service.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;
import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.ApiExplorer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private final ApiExplorer explorer;

    @Override
    public List<SearchHospitalDTO> searchHospital(String hospitalName) throws IOException {
        StringBuilder hospitalInfo = explorer.callHospitalAPI(hospitalName);
        return parseHospitalInfo(hospitalInfo);
    }

    @Override
    public List<SearchHospitalDTO> parseHospitalInfo(StringBuilder hospitalInfo)
            throws JsonProcessingException {
        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(hospitalInfo.toString());

        if (rootNode.path("response").path("header").path("body").path("totalCount").asInt() == 0) {
            throw new HospitalException(ErrorStatus.HOSPITAL_NOT_FOUND);
        }

        JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

        List<SearchHospitalDTO> hospitalDTOList = new ArrayList<>();
        for (JsonNode itemNode : itemsNode) {
            String name = itemNode.path("yadmNm").asText();
            String address = itemNode.path("addr").asText();
            String ykiho = itemNode.path("ykiho").asText();
            SearchHospitalDTO hospitalDTO =
                    SearchHospitalDTO.builder().name(name).address(address).ykiho(ykiho).build();
            hospitalDTOList.add(hospitalDTO);
        }
        return hospitalDTOList;
    }
}
