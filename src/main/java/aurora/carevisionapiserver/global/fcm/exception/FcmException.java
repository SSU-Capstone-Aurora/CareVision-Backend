package aurora.carevisionapiserver.global.fcm.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class FcmException extends GeneralException {
    public FcmException(BaseErrorCode code) {
        super(code);
    }
}
