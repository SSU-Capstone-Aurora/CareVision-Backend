package aurora.carevisionapiserver.domain.patient.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientRegisterRequest;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientSearchListResponse;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
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
    @RefreshTokenApiResponse
    @GetMapping("/search")
    public BaseResponse<PatientSearchListResponse> searchPatient(
            @RequestParam(name = "search") String patientName) {
        Map<PatientDocument, Bed> patients = patientService.searchPatient(patientName);
        return BaseResponse.onSuccess(PatientConverter.toPatientSearchListResponse(patients));
    }

    @Operation(summary = "환자 조회 API", description = "환자 리스트를 조회합니다.(최신등록순)_숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @GetMapping("")
    public BaseResponse<PatientSearchListResponse> getPatients(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin) {
        List<Patient> patients = patientService.getPatients(admin.getId());
        return BaseResponse.onSuccess(PatientConverter.toPatientSearchListResponse(patients));
    }

    @Operation(summary = "환자 등록 API", description = "환자명을 입력하고, 카메라를 선택하여 환자를 등록합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON201", description = "OK, 요청 성공 및 리소스 생성됨."),
    })
    @RefreshTokenApiResponse
    @PostMapping("")
    public BaseResponse<Void> createPatient(
            @Parameter(name = "admin", hidden = true) @AuthUser Admin admin,
            @RequestBody PatientRegisterRequest patientRegisterRequest) {

        PatientCreateRequest patientCreateRequest = patientRegisterRequest.getPatient();
        CameraSelectRequest cameraSelectRequest = patientRegisterRequest.getCamera();

        patientService.createAndConnectPatient(patientCreateRequest, cameraSelectRequest, admin);

        return BaseResponse.of(SuccessStatus._CREATED, null);
    }
}
