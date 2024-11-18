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
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEPARTMENT400", "과를 찾을 수 없습니다."),

    // Nurse
    NURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "NURSE400", "간호사를 찾을 수 없습니다."),

    // Patient
    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PATIENT400", "환자를 찾을 수 없습니다."),
    PATIENT_DUPLICATED(HttpStatus.BAD_REQUEST, "PATIENT401", "이미 존재하는 환자입니다"),

    // Auth
    USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "AUTH400", "이미 사용 중인 아이디입니다."),
    REFRESH_TOKEN_NULL(HttpStatus.BAD_REQUEST, "AUTH401", "refresh token이 null입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUTH402", "refresh token이 인식되지 않았습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH403", "refresh token이 만료되었습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH404", "인증에 실패하였습니다."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH405", "해당하는 유저 이름을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH406", "해당하는 유저를 찾을 수 없습니다."),
    USER_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "AUTH407", "승인되지 않은 유저입니다."),
    INVALID_ROLE(HttpStatus.FORBIDDEN, "AUTH408", "유효하지 않은 권한입니다."),

    // Admin
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN400", "관리자를 찾을 수 없습니다."),

    // Camera
    CAMERA_NOT_FOUND(HttpStatus.NOT_FOUND, "CAMERA400", "카메라를 찾을 수 없습니다."),

    // Bed
    INVALID_BED_INFO(
            HttpStatus.BAD_REQUEST, "BED400", "잘못된 형식의 베드 정보입니다. '동 호 번' 또는 '호 번' 순서로 작성해 주세요."),

    // fcm
    CLIENT_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "FCM400", "token이 만료되었습니다."),
    CLIENT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM401", "token을 찾을 수 없습니다."),
    EXECUTION_FAILED(HttpStatus.BAD_REQUEST, "FCM402", "FireStore에서 데이터를 불러오는 실행 도중 오류가 발생하였습니다."),
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
