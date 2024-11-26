package aurora.carevisionapiserver.domain.admin.api;

import static aurora.carevisionapiserver.domain.hospital.converter.HospitalConverter.toHospitalInfoResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalInfoResponse;
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

@Tag(name = "Admin 🧑‍💼", description = "관리자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    @Operation(summary = "관리자 프로필 조회 API", description = "관리자 프로필을 조회합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @GetMapping("/profile")
    public BaseResponse<HospitalInfoResponse> getNurseProfile(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin) {
        return BaseResponse.of(SuccessStatus._OK, toHospitalInfoResponse(admin));
    }
}
