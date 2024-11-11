package aurora.carevisionapiserver.domain.patient.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class CameraException extends GeneralException {
    public CameraException(BaseErrorCode code) {
        super(code);
    }
}
