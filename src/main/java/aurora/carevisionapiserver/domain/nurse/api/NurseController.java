package aurora.carevisionapiserver.domain.nurse.api;

import java.util.HashMap;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseAcceptanceRetryRequest;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseProfileResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientRegisterRequest;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientSelectRequest;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientProfileListResponse;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmPreviewResponse;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmResponse.AlarmInfoListResponse;
import aurora.carevisionapiserver.global.fcm.service.FcmService;
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

@Tag(name = "Nurse 💉", description = "간호사 관련 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class NurseController {
    private final PatientService patientService;
    private final NurseService nurseService;
    private final FcmService fcmService;

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
    public BaseResponse<Object> connectPatient(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @RequestBody PatientSelectRequest patientSelectRequest) {
        Patient patient = patientService.getPatient(patientSelectRequest.getPatientId());
        nurseService.connectPatient(nurse, patient);
        return BaseResponse.of(SuccessStatus._CREATED, new HashMap<>());
    }

    @Operation(
            summary = "새로운 담당 환자 등록 API",
            description = "연결할 환자명이 없을 때, 환자명을 입력하고, 카메라를 선택하여 새로운 환자를 등록합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON201", description = "OK, 요청 성공 및 리소스 생성됨."),
    })
    @RefreshTokenApiResponse
    @PostMapping("/patients")
    public BaseResponse<Object> createPatient(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @RequestBody PatientRegisterRequest patientRegisterRequest) {
        PatientCreateRequest patientCreateRequest = patientRegisterRequest.getPatient();
        CameraSelectRequest cameraSelectRequest = patientRegisterRequest.getCamera();

        patientService.createAndConnectPatient(patientCreateRequest, cameraSelectRequest, nurse);

        return BaseResponse.of(SuccessStatus._CREATED, new HashMap<>());
    }

    @Operation(summary = "담당 환자 퇴원 API", description = "환자를 퇴원합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
        @ApiResponse(responseCode = "PATIENT400", description = "환자를 찾을 수 없습니다.")
    })
    @RefreshTokenApiResponse
    @DeleteMapping("patients/{patientId}")
    public BaseResponse<Object> deletePatient(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @PathVariable Long patientId) {
        patientService.deletePatient(patientId);
        return BaseResponse.of(SuccessStatus._NO_CONTENT, new HashMap<>());
    }

    @Operation(
            summary = "아직 간호사와 연결되지 않은 환자 리스트 조회 API",
            description = "등록되었지만 아직 간호사와 연결되지 않은 환자 리스트를 조회합니다._예림")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON202", description = "OK, 요청 성공 및 반환할 콘텐츠 없음"),
    })
    @RefreshTokenApiResponse
    @GetMapping("/patients/unlinked")
    public BaseResponse<PatientProfileListResponse> getUnlinkedPatientList(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        List<Patient> patients = patientService.getUnlinkedPatients(nurse);
        return BaseResponse.of(
                SuccessStatus._OK, PatientConverter.toPatientProfileListResponse(patients));
    }

    @Operation(summary = "간호사의 알람 리스트 조회 API", description = "간호사의 이상행동 감지 알람 리스트를 조회합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(
                responseCode = "FCM402",
                description = "EXECUTION_FAILED, FireStore에서 데이터를 불러오는 실행 도중 오류가 발생하였습니다.")
    })
    @RefreshTokenApiResponse
    @GetMapping("/alarms")
    public BaseResponse<AlarmInfoListResponse> getAlarmsInfo(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        return BaseResponse.of(SuccessStatus._OK, fcmService.getAlarmsInfo(nurse));
    }

    @Operation(summary = "간호사의 알람 상세 조회 API", description = "이상행동 감지 알람을 조회합니다._숙희")
    @ApiResponses({@ApiResponse(responseCode = "COMMON200", description = "OK, 성공")})
    @RefreshTokenApiResponse
    @GetMapping("/alarm")
    public BaseResponse<StreamingInfoResponse> getAlarmsInfo(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse,
            @RequestParam(name = "documentId") String documentId) {
        return BaseResponse.of(SuccessStatus._OK, fcmService.getAlarmInfo(nurse, documentId));
    }

    @Operation(summary = "미확인 알림 개수 조회 API", description = "간호사의 미확인 이상행동 알림 개수를 조회합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "_OK,성공입니다."),
        @ApiResponse(responseCode = "FCM400", description = "BAD_REQUEST, 토큰이 만료되었습니다"),
    })
    @GetMapping("/alarm-count")
    public BaseResponse<AlarmPreviewResponse> getAlarmCount(
            @Parameter(name = "nurse", hidden = true) @AuthUser Nurse nurse) {
        return BaseResponse.of(SuccessStatus._OK, fcmService.getAlarmCount(nurse));
    }

    @Operation(summary = "간호사의 수락 재요청 API", description = "간호사가 수락을 재요청합니다_예림")
    @ApiResponses({
        @ApiResponse(responseCode = "NURSE200", description = "OK, 재요청이 정상적으로 처리되었습니다."),
        @ApiResponse(responseCode = "NURSE400", description = "NOT_FOUND, 간호사를 찾을 수 없습니다.")
    })
    @RefreshTokenApiResponse
    @PostMapping("/requests/retry")
    public BaseResponse<Object> retryNurseAcceptanceRequest(
            @RequestBody NurseAcceptanceRetryRequest nurseAcceptanceRetryRequest) {
        nurseService.retryAcceptanceRequest(nurseAcceptanceRetryRequest.getUsername());
        return BaseResponse.of(SuccessStatus.NURSE_REQUEST_RETRIED, new HashMap<>());
    }
}
