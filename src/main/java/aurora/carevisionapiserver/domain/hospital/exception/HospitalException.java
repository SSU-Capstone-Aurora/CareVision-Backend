package aurora.carevisionapiserver.domain.hospital.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class HospitalException extends GeneralException {

    public HospitalException(BaseErrorCode code) {
        super(code);
    }
}
