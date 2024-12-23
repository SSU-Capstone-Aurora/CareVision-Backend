package aurora.carevisionapiserver.domain.nurse.api;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseRegisterRequestCountResponse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseRegisterRequestListResponse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NursePreviewListResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.AuthUser;
import aurora.carevisionapiserver.global.security.handler.annotation.RefreshTokenApiResponse;
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
    @RefreshTokenApiResponse
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
    @RefreshTokenApiResponse
    @GetMapping("/nurses/search")
    public BaseResponse<NursePreviewListResponse> searchNurse(
            @RequestParam(name = "search") String nurseName) {
        List<NurseDocument> nurses = nurseService.searchNurse(nurseName);
        return BaseResponse.onSuccess(NurseConverter.toNurseDocumentPreviewListResponse(nurses));
    }

    @Operation(summary = "간호사 요청 리스트 조회 API", description = "간호사 등록 요청 리스트를 조회합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT_FOUND, 간호사를 찾을 수 없습니다."),
    })
    @RefreshTokenApiResponse
    @GetMapping("/nurses/requests")
    public BaseResponse<NurseRegisterRequestListResponse> getNurseRegisterRequestList(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin) {
        List<Nurse> nurses = nurseService.getInactiveNurses(admin);
        return BaseResponse.onSuccess(NurseConverter.toNurseRegisterRequestListResponse(nurses));
    }

    @Operation(summary = "간호사 요청 수락 API", description = "간호사 등록 요청을 수락합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT_FOUND, 간호사를 찾을 수 없습니다."),
    })
    @RefreshTokenApiResponse
    @PostMapping("/nurses/requests/{nurseId}")
    public BaseResponse<Object> acceptNurseRegisterRequest(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @PathVariable Long nurseId) {
        nurseService.activateNurse(nurseId);
        return BaseResponse.of(SuccessStatus.ACCEPTED, new HashMap<>());
    }

    @Operation(summary = "간호사 요청 거부 API", description = "간호사 등록 요청을 거부합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT_FOUND, 간호사를 찾을 수 없습니다."),
    })
    @RefreshTokenApiResponse
    @DeleteMapping("/nurses/requests/{nurseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BaseResponse<Object> deleteInactiveNurse(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @PathVariable Long nurseId) {
        nurseService.deleteInactiveNurse(nurseId);
        return BaseResponse.of(SuccessStatus._NO_CONTENT, new HashMap<>());
    }

    @Operation(summary = "간호사 등록 요청 수 조회 API", description = "간호사 등록 요청 수를 조회합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @GetMapping("/nurses/requests/count")
    public BaseResponse<NurseRegisterRequestCountResponse> getNurseRegisterRequestCount(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin) {
        long requestCount = nurseService.getNurseRegisterRequestCount(admin);
        return BaseResponse.onSuccess(
                NurseConverter.toNurseRegisterRequestCountResponse(requestCount));
    }

    @Operation(summary = "간호사 삭제 API", description = "간호사를 삭제합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
    })
    @RefreshTokenApiResponse
    @DeleteMapping("/nurses/{nurseId}")
    public BaseResponse<Object> deleteActiveNurse(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @PathVariable Long nurseId) {
        nurseService.deleteActiveNurse(nurseId);
        return BaseResponse.of(SuccessStatus._NO_CONTENT, new HashMap<>());
    }
}
