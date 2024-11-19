package aurora.carevisionapiserver.domain.hospital.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.DepartmentCreateRequest;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalSearchResponse;

public interface HospitalService {

    Hospital createHospital(HospitalCreateRequest hospitalCreateRequest);

    Department createDepartment(DepartmentCreateRequest departmentCreateRequest, Hospital hospital);

    List<HospitalSearchResponse> searchHospital(String hospitalName) throws IOException;

    List<HospitalSearchResponse> parseHospitalInfo(StringBuilder hospitalInfo)
            throws JsonProcessingException;

    List<String> searchDepartment(String ykiho) throws IOException;

    List<String> parseDepartmentInfo(StringBuilder departmentInfo) throws IOException;

    Hospital getHospital(Long id);

    Department getDepartment(Long id);

    Department getDepartment(Hospital hospital);

    List<Hospital> getHospitals();

    Map<Long, String> getDepartments(Long hospitalId);
}
