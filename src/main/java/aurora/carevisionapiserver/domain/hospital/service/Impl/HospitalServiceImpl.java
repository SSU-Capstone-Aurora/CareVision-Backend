package aurora.carevisionapiserver.domain.hospital.service.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.hospital.converter.HospitalConverter;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.DepartmentCreateRequest;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalSearchResponse;
import aurora.carevisionapiserver.domain.hospital.exception.DepartmentException;
import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.domain.hospital.repository.DepartmentRepository;
import aurora.carevisionapiserver.domain.hospital.repository.HospitalRepository;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.ApiExplorer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private final ApiExplorer explorer;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Hospital createHospital(HospitalCreateRequest hospitalCreateRequest) {
        // 병원이 이미 존재하는지 확인 (ykiho를 기준으로)
        Hospital existingHospital = getHospital(hospitalCreateRequest.getYkiho());

        if (existingHospital != null) {
            // 이미 존재하는 병원 반환
            return existingHospital;
        }

        // 병원이 없으면 새로 생성
        Hospital newHosptial = HospitalConverter.toHospital(hospitalCreateRequest);
        return hospitalRepository.save(newHosptial);
    }

    private Hospital getHospital(String ykiho) {
        return hospitalRepository.findByYkiho(ykiho).orElse(null);
    }

    @Override
    public Department createDepartment(
            DepartmentCreateRequest departmentCreateRequest, Hospital hospital) {
        Department department =
                HospitalConverter.toDepartment(departmentCreateRequest.getName(), hospital);
        return departmentRepository.save(department);
    }

    @Override
    public List<HospitalSearchResponse> searchHospital(String hospitalName) throws IOException {
        StringBuilder hospitalInfo = explorer.callHospitalAPI(hospitalName);
        return parseHospitalInfo(hospitalInfo);
    }

    @Override
    public List<HospitalSearchResponse> parseHospitalInfo(StringBuilder hospitalInfo)
            throws JsonProcessingException {
        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(hospitalInfo.toString());

        checkTotalCount(rootNode);

        JsonNode itemsNode = getItemsNode(rootNode);

        List<HospitalSearchResponse> hospitalSearchListResponse = new ArrayList<>();
        for (JsonNode itemNode : itemsNode) {
            String name = itemNode.path("yadmNm").asText();
            String address = itemNode.path("addr").asText();
            String ykiho = itemNode.path("ykiho").asText();
            HospitalSearchResponse hospitalResponse =
                    HospitalSearchResponse.builder()
                            .name(name)
                            .address(address)
                            .ykiho(ykiho)
                            .build();
            hospitalSearchListResponse.add(hospitalResponse);
        }
        return hospitalSearchListResponse;
    }

    @Override
    public List<String> searchDepartment(String ykiho) throws IOException {
        StringBuilder departmentInfo = explorer.callDepartmentAPI(ykiho);
        return parseDepartmentInfo(departmentInfo);
    }

    @Override
    public List<String> parseDepartmentInfo(StringBuilder departmentInfo) throws IOException {
        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(departmentInfo.toString());

        checkTotalCount(rootNode);

        JsonNode itemsNode = getItemsNode(rootNode);

        List<String> departments = new ArrayList<>();
        for (JsonNode itemNode : itemsNode) {
            String department = itemNode.path("dgsbjtCdNm").asText();
            departments.add(department);
        }
        return departments;
    }

    public void checkTotalCount(JsonNode rootNode) {
        if (rootNode.path("response").path("body").path("totalCount").asInt() == 0) {
            throw new HospitalException(ErrorStatus.HOSPITAL_NOT_FOUND);
        }
    }

    public JsonNode getItemsNode(JsonNode rootNode) {
        return rootNode.path("response").path("body").path("items").path("item");
    }

    @Override
    public Hospital getHospital(Long id) {
        return hospitalRepository
                .findById(id)
                .orElseThrow(() -> new HospitalException(ErrorStatus.HOSPITAL_NOT_FOUND));
    }

    @Override
    public Department getDepartment(Long id) {
        return departmentRepository
                .findById(id)
                .orElseThrow(() -> new DepartmentException(ErrorStatus.DEPARTMENT_NOT_FOUND));
    }

    @Override
    public Department getDepartment(Hospital hospital) {
        return departmentRepository
                .findByHospital(hospital)
                .orElseThrow(() -> new DepartmentException(ErrorStatus.DEPARTMENT_NOT_FOUND));
    }

    @Override
    public List<Hospital> getHospitals() {
        return hospitalRepository.findAll();
    }

    @Override
    public Map<Long, String> getDepartments(Long hospitalId) {
        Hospital hospital = getHospital(hospitalId);
        return hospital.getDepartments().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
    }
}
