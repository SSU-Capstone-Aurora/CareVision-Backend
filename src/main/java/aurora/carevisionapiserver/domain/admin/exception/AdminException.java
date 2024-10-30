package aurora.carevisionapiserver.domain.admin.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class AdminException extends GeneralException {

    public AdminException(BaseErrorCode code) {
        super(code);
    }
}
