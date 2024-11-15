package aurora.carevisionapiserver.domain.hospital.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class DepartmentException extends GeneralException {

    public DepartmentException(BaseErrorCode code) {
        super(code);
    }
}
