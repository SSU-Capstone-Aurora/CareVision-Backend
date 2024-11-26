package aurora.carevisionapiserver.domain.nurse.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class NurseException extends GeneralException {
    public NurseException(BaseErrorCode code) {
        super(code);
    }
}
