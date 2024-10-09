package aurora.carevisionapiserver.domain.hospital.api;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.hospital.converter.HospitalConverter;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTOList;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin 🧑‍💼", description = "관리자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminHospitalController {
    private final HospitalService hospitalService;

    @Operation(summary = "병원 명 조회 API", description = "병원 명을 받아와 조회합니다_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "HOSPITAL400", description = "NOT_FOUND, 병원을 찾을 수 없습니다."),
    })
    @GetMapping("/hospitals")
    public BaseResponse<SearchHospitalDTOList> searchHospital(
            @RequestParam(name = "search") String hospitalName) throws IOException {
        List<SearchHospitalDTO> hospitalDTOList = hospitalService.searchHospital(hospitalName);
        return BaseResponse.of(
                SuccessStatus._OK, HospitalConverter.toSearchHospitalDTOList(hospitalDTOList));
    }
}
