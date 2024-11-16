package aurora.carevisionapiserver.global.fcm.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class FcmException extends GeneralException {
    public FcmException(BaseErrorCode code) {
        super(code);
    }
}
