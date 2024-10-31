package aurora.carevisionapiserver.domain.nurse.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseRegisterRequestListResponse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewListResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.util.validation.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin) {
        List<Nurse> nurses = nurseService.getActiveNurses(admin);
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
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin) {
        List<Nurse> nurses = nurseService.getInActiveNurses(admin);
        return BaseResponse.onSuccess(NurseConverter.toNurseRegisterRequestListResponse(nurses));
    }

    @Operation(summary = "간호사 요청 수락 API", description = "간호사 등록 요청을 수락합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT_FOUND, 간호사를 찾을 수 없습니다."),
    })
    @PostMapping("/requests/{nurseId}")
    public BaseResponse<Void> acceptNurseRequest(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @PathVariable Long nurseId) {
        nurseService.activateNurse(admin, nurseId);
        return BaseResponse.of(SuccessStatus.ACCEPTED, null);
    }

    @Operation(summary = "간호사 요청 거부 API", description = "간호사 등록 요청을 거부합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT_FOUND, 간호사를 찾을 수 없습니다."),
    })
    @DeleteMapping("/requests/{nurseId}")
    public BaseResponse<Void> deleteNurse(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @PathVariable Long nurseId) {
        nurseService.deleteNurse(admin, nurseId);
        return BaseResponse.of(SuccessStatus._NO_CONTENT, null);
    }
}
