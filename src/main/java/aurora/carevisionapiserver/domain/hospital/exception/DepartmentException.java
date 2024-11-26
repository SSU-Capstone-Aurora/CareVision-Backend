package aurora.carevisionapiserver.domain.hospital.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class DepartmentException extends GeneralException {

    public DepartmentException(BaseErrorCode code) {
        super(code);
    }
}
