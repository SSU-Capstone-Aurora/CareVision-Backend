package aurora.carevisionapiserver.domain.patient.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class CameraException extends GeneralException {
    public CameraException(BaseErrorCode code) {
        super(code);
    }
}
