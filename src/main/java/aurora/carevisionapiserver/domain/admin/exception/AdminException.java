package aurora.carevisionapiserver.domain.admin.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class AdminException extends GeneralException {

    public AdminException(BaseErrorCode code) {
        super(code);
    }
}
