package aurora.carevisionapiserver.domain.nurse.api;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.domain.nurse.converter.NurseConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.response.NurseResponse.NurseProfileResponse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.converter.PatientConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.response.PatientResponse.PatientProfileListResponse;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.SuccessStatus;
import aurora.carevisionapiserver.global.util.validation.ExistNurse;
import io.swagger.v3.oas.annotations.Operation;
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
    private final NurseService nurseService;
    private final PatientService patientService;

    @Operation(summary = "간호사 마이페이지 API", description = "간호사 마이페이지를 조회합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @ApiResponse(responseCode = "NURSE400", description = "NOT FOUND, 간호사를 찾을 수 없음")
    })
    @GetMapping("/profile")
    public BaseResponse<NurseProfileResponse> getNurseProfile(
            @ExistNurse @RequestParam(name = "nurseId") Long nurseId) {
        Nurse nurse = nurseService.getNurse(nurseId);
        return BaseResponse.of(SuccessStatus._OK, NurseConverter.toNurseProfileResponse(nurse));
    }

    @Operation(summary = "담당 환자 리스트 조회 API", description = "간호사가 담당하는 환자 리스트를 조회합니다._숙희")
    @ApiResponses({
        @ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("/patients")
    public BaseResponse<PatientProfileListResponse> getPatientList(
            @ExistNurse @RequestParam(name = "nurseId") Long nurseId) {
        Nurse nurse = nurseService.getNurse(nurseId);
        List<Patient> patients = patientService.getPatients(nurse);
        return BaseResponse.of(
                SuccessStatus._OK, PatientConverter.toPatientProfileListResponse(patients));
    }
}
