package aurora.carevisionapiserver.domain.admin.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.admin.converter.AdminConverter;
import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminCreateRequest;
import aurora.carevisionapiserver.domain.admin.dto.request.AdminRequest.AdminSignUpRequest;
import aurora.carevisionapiserver.domain.admin.dto.response.AdminResponse.AdminSignUpResponse;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.hospital.dto.request.HospitalRequest.HospitalCreateRequest;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth 🔐", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminAuthController {
    private final AdminService adminService;
    private final HospitalService hospitalService;

    @Operation(summary = "관리자 회원가입 API", description = "관리자가 회원가입합니다_예림")
    @ApiResponses({@ApiResponse(responseCode = "COMMON200", description = "OK, 성공")})
    @PostMapping("/sign-up")
    public BaseResponse<AdminSignUpResponse> createAdmin(
            @RequestBody AdminSignUpRequest adminSignUpRequest) {

        AdminCreateRequest adminCreateRequest = adminSignUpRequest.getAdmin();
        HospitalCreateRequest hospitalCreateRequest = adminSignUpRequest.getHospital();

        Hospital hospital = hospitalService.createHospital(hospitalCreateRequest);
        Admin admin = adminService.createAdmin(adminCreateRequest, hospital);

        return BaseResponse.onSuccess(AdminConverter.toAdminSignUpResponse(admin));
    }

    @Operation(summary = "관리자 회원가입 중복 체크", description = "주어진 아이디가 이미 존재하는지 확인합니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "AUTH400", description = "아이디가 이미 존재합니다.")
    })
    @GetMapping("/check-username")
    public BaseResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean isDuplicated = adminService.isUsernameDuplicated(username);

        if (isDuplicated) {
            return BaseResponse.onFailure(
                    ErrorStatus.USERNAME_DUPLICATED.getCode(),
                    ErrorStatus.USERNAME_DUPLICATED.getMessage(),
                    false);
        } else {
            return BaseResponse.of(SuccessStatus._USERNAME_AVAILABLE, true);
        }
    }
}
