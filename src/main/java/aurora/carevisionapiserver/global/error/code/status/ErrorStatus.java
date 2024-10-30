package aurora.carevisionapiserver.global.error.code.status;

import org.springframework.http.HttpStatus;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Hospital
    HOSPITAL_NOT_FOUND(HttpStatus.NOT_FOUND, "HOSPITAL400", "병원을 찾을 수 없습니다."),

    // Nurse
    NURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "NURSE400", "간호사를 찾을 수 없습니다."),

    // Patient
    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PATIENT400", "환자를 찾을 수 없습니다."),

    // Auth
    USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "AUTH400", "이미 사용 중인 아이디입니다."),
    // Admin
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN400", "관리자를 찾을 수 없습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
