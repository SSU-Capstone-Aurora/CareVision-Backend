package aurora.carevisionapiserver.global.auth.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(BaseErrorCode code) {
        super(code);
    }
}
