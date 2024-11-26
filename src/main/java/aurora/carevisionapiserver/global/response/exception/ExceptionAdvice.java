package aurora.carevisionapiserver.global.response.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.response.code.ErrorReasonDTO;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
@RequiredArgsConstructor
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    private ResponseEntity<Object> handleException(
            Exception e,
            ErrorStatus errorStatus,
            String errorMessage,
            HttpHeaders headers,
            WebRequest request) {
        BaseResponse<Object> body =
                BaseResponse.onFailure(
                        errorStatus.getCode(), errorStatus.getMessage(), errorMessage);
        return super.handleExceptionInternal(
                e, body, headers, errorStatus.getHttpStatus(), request);
    }

    private ResponseEntity<Object> handleExceptionInternal(
            Exception e, ErrorReasonDTO reason, HttpHeaders headers, HttpServletRequest request) {

        BaseResponse<Object> body =
                BaseResponse.onFailure(reason.getCode(), reason.getMessage(), null);

        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(e, body, headers, reason.getHttpStatus(), webRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        return handleException(
                e, ErrorStatus._INTERNAL_SERVER_ERROR, e.getMessage(), HttpHeaders.EMPTY, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        String errorMessage =
                e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((first, second) -> first + ", " + second)
                        .orElse("Validation error occurred");

        return handleExceptionInternalConstraint(
                e, ErrorStatus.valueOf(errorMessage), HttpHeaders.EMPTY, request);
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(
            Exception e, ErrorStatus errorCommonStatus, HttpHeaders headers, WebRequest request) {
        BaseResponse<Object> body =
                BaseResponse.onFailure(
                        errorCommonStatus.getCode(), errorCommonStatus.getMessage(), null);
        return super.handleExceptionInternal(
                e, body, headers, errorCommonStatus.getHttpStatus(), request);
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> onThrowException(
            GeneralException generalException, HttpServletRequest request) {
        ErrorReasonDTO errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(
                        fieldError ->
                                errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return handleExceptionInternalArgs(e, request, errors);
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(
            Exception e, WebRequest request, Map<String, String> errorArgs) {
        BaseResponse<Object> body =
                BaseResponse.onFailure(
                        ErrorStatus._BAD_REQUEST.getCode(),
                        ErrorStatus._BAD_REQUEST.getMessage(),
                        errorArgs);
        return super.handleExceptionInternal(
                e, body, HttpHeaders.EMPTY, ErrorStatus._BAD_REQUEST.getHttpStatus(), request);
    }
}
