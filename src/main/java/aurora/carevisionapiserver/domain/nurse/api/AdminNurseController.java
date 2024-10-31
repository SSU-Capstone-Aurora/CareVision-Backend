package aurora.carevisionapiserver.domain.nurse.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseRegisterRequestListResponse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewListResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin-Nurse 💉", description = "관리자 - 간호사 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminNurseController {
    private final NurseService nurseService;

    @Operation(summary = "간호사 리스트 조회 API", description = "전체 간호사 리스트를 조회합니다 (등록 최신순)_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("/nurses")
    public BaseResponse<NursePreviewListResponse> getNurseList(
            @RequestParam(name = "adminId") Long adminId) {
        List<Nurse> nurses = nurseService.getActiveNurses(adminId);
        return BaseResponse.onSuccess(NurseConverter.toNursePreviewListResponse(nurses));
    }

    @Operation(summary = "간호사 검색 API", description = "입력받은 간호사 명으로 간호사를 검색합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("/nurses/search")
    public BaseResponse<NursePreviewListResponse> searchNurse(
            @RequestParam(name = "search") String nurseName) {
        List<Nurse> nurses = nurseService.searchNurse(nurseName);
        return BaseResponse.onSuccess(NurseConverter.toNursePreviewListResponse(nurses));
    }

    @Operation(summary = "간호사 요청 리스트 API", description = "간호사 등록 요청 리스트를 조회합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT_FOUND, 간호사를 찾을 수 없습니다."),
    })
    @GetMapping("/requests")
    public BaseResponse<NurseRegisterRequestListResponse> getNurseRequestList(
            @RequestParam(name = "adminId") Long adminId) {
        List<Nurse> nurses = nurseService.getInActiveNurses(adminId);
        return BaseResponse.onSuccess(NurseConverter.toNurseRegisterRequestListResponse(nurses));
    }
}
