package aurora.carevisionapiserver.domain.bed.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class BedException extends GeneralException {
    public BedException(BaseErrorCode code) {
        super(code);
    }
}
