package aurora.carevisionapiserver.global.response.code.status;

import org.springframework.http.HttpStatus;

import aurora.carevisionapiserver.global.response.code.BaseCode;
import aurora.carevisionapiserver.global.response.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "요청 성공 및 리소스 생성됨"),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON202", "요청 성공 및 반환할 콘텐츠가 없음"),

    // Auth
    USERNAME_AVAILABLE(HttpStatus.OK, "AUTH200", "사용 가능한 아이디입니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH201", "성공적으로 로그인 되었습니다."),
    REFRESH_TOKEN_ISSUED(HttpStatus.OK, "AUTH202", "refresh token이 발급되었습니다."),

    // Admin
    ACCEPTED(HttpStatus.OK, "ADMIN200", "요청이 성공적으로 수락되었습니다."),

    // Nurse
    NURSE_REQUEST_RETRIED(HttpStatus.OK, "NURSE200", "재요청이 정상적으로 처리되었습니다."),

    // Camera
    FIREBASE_TOKEN_SUCCESS(HttpStatus.OK, "FCM200", "클라이언트 토큰 저장 완료"),
    ALARM_SUCCESS(HttpStatus.OK, "FCM201", "이상행동 감지 알림 전송 완료"),
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
