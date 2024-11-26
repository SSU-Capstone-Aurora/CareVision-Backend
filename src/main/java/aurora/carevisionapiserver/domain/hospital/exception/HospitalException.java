package aurora.carevisionapiserver.domain.hospital.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class HospitalException extends GeneralException {

    public HospitalException(BaseErrorCode code) {
        super(code);
    }
}
