package aurora.carevisionapiserver.domain.patient.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.util.validation.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin-Patient 🤒", description = "관리자 - 환자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/patients")
public class AdminPatientController {
    private final PatientService patientService;

    @Operation(summary = "환자 검색 API", description = "입력받은 환자명으로 환자를 검색합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "PATIENT400", description = "NOT FOUND, 환자가 없습니다.")
    })
    @GetMapping("/search")
    public BaseResponse<PatientSearchListResponse> searchPatient(
            @RequestParam(name = "search") String patientName) {
        List<Patient> patients = patientService.searchPatient(patientName);
        return BaseResponse.onSuccess(PatientConverter.toPatientSearchListResponse(patients));
    }

    @Operation(summary = "환자 조회 API", description = "환자 리스트를 조회합니다.(최신등록순)_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("")
    public BaseResponse<PatientSearchListResponse> getPatients(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin) {
        List<Patient> patients = patientService.getPatients(admin.getId());
        return BaseResponse.onSuccess(PatientConverter.toPatientSearchListResponse(patients));
    }
}
