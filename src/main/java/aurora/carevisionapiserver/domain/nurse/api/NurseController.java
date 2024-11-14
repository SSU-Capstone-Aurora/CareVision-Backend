package aurora.carevisionapiserver.domain.nurse.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseProfileResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientRegisterRequest;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientSelectRequest;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientProfileListResponse;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.util.validation.annotation.AuthUser;
import aurora.carevisionapiserver.global.util.validation.annotation.RefreshTokenApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Nurse 💉", description = "간호사 관련 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class NurseController {
    private final PatientService patientService;
    private final NurseService nurseService;
    private final CameraService cameraService;

    @Operation(summary = "간호사 마이페이지 API", description = "간호사 마이페이지를 조회합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT FOUND, 간호사를 찾을 수 없음")
    })
    @RefreshTokenApiResponse
    @GetMapping("/profile")
    public BaseResponse<NurseProfileResponse> getNurseProfile(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        return BaseResponse.of(SuccessStatus._OK, NurseConverter.toNurseProfileResponse(nurse));
    }

    @Operation(summary = "담당 환자 리스트 조회 API", description = "간호사가 담당하는 환자 리스트를 조회합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @RefreshTokenApiResponse
    @GetMapping("/patients")
    public BaseResponse<PatientProfileListResponse> getPatientList(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        List<Patient> patients = patientService.getPatients(nurse);
        return BaseResponse.of(
                SuccessStatus._OK, PatientConverter.toPatientProfileListResponse(patients));
    }

    @Operation(
            summary = "기존 환자 선택 및 연결 API",
            description = "환자 리스트에서 환자를 선택해 간호사가 담당하는 환자를 등록합니다._숙희, 예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON201", description = "OK, 성공"),
        @ApiResponse(responseCode = "PATIENT400", description = "환자를 찾을 수 없습니다"),
    })
    @RefreshTokenApiResponse
    @PatchMapping("/patients")
    public BaseResponse<String> connectPatient(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @RequestBody PatientSelectRequest patientSelectRequest) {
        Patient patient = patientService.getPatient(patientSelectRequest.getPatientId());
        nurseService.connectPatient(nurse, patient);
        return BaseResponse.of(SuccessStatus._CREATED, null);
    }

    @Operation(
            summary = "새로운 담당 환자 등록 API",
            description = "연결할 환자명이 없을 때, 환자명을 입력하고, 카메라를 선택하여 새로운 환자를 등록합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON201", description = "OK, 요청 성공 및 리소스 생성됨."),
        @ApiResponse(responseCode = "BED400", description = "BAD REQUEST, 잘못된 형식의 베드 정보입니다."),
    })
    @RefreshTokenApiResponse
    @PostMapping("/patients")
    public BaseResponse<Void> createPatient(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @RequestBody PatientRegisterRequest patientRegisterRequest) {
        PatientCreateRequest patientCreateRequest = patientRegisterRequest.getPatient();
        CameraSelectRequest cameraSelectRequest = patientRegisterRequest.getCamera();

        Patient patient = patientService.createPatient(patientCreateRequest);
        Camera camera = cameraService.getCamera(cameraSelectRequest.getId());
        cameraService.connectPatient(camera, patient);
        nurseService.connectPatient(nurse, patient);
        return BaseResponse.of(SuccessStatus._CREATED, null);
    }

    @Operation(summary = "담당 환자 퇴원 API", description = "환자를 퇴원합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
        @ApiResponse(responseCode = "PATIENT400", description = "환자를 찾을 수 없습니다.")
    })
    @RefreshTokenApiResponse
    @DeleteMapping("/{patientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BaseResponse<Void> deletePatient(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @PathVariable Long patientId) {
        patientService.deletePatient(patientId);
        return BaseResponse.of(SuccessStatus._NO_CONTENT, null);
    }
}
