package aurora.carevisionapiserver.global.error.code.status;

import org.springframework.http.HttpStatus;

import aurora.carevisionapiserver.global.error.code.BaseCode;
import aurora.carevisionapiserver.global.error.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "요청 성공 및 리소스 생성됨"),

    // Auth
    USERNAME_AVAILABLE(HttpStatus.OK, "AUTH200", "사용 가능한 아이디입니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH201", "성공적으로 로그인 되었습니다."),
    REFRESH_TOKEN_ISSUED(HttpStatus.OK, "AUTH202", "refresh token이 발급되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder().message(message).code(code).isSuccess(true).build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
