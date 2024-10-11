package aurora.carevisionapiserver.domain.nurse.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class NurseException extends GeneralException {
    public NurseException(BaseErrorCode code) {
        super(code);
    }
}
