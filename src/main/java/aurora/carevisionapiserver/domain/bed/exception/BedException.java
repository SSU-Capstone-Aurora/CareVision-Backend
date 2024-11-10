package aurora.carevisionapiserver.domain.bed.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class BedException extends GeneralException {
    public BedException(BaseErrorCode code) {
        super(code);
    }
}
