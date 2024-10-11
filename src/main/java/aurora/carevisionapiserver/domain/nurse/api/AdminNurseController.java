package aurora.carevisionapiserver.domain.nurse.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.NurseDTO.NursePreviewDTOList;
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
@RequestMapping("/api/admin/nurses")
public class AdminNurseController {
    private final NurseService nurseService;

    @Operation(summary = "간호사 리스트 조회 API", description = "전체 간호사 리스트를 조회합니다 (등록 최신순)_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT FOUND, 간호사를 찾을 수 없음")
    })
    @GetMapping("")
    public BaseResponse<NursePreviewDTOList> getNurseList() {
        List<Nurse> nurses = nurseService.getNurseList();
        return BaseResponse.onSuccess(NurseConverter.toNursePreviewDTOList(nurses));
    }
}
