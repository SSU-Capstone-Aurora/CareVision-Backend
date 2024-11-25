package aurora.carevisionapiserver.domain.patient.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class PatientException extends GeneralException {
    public PatientException(BaseErrorCode code) {
        super(code);
    }
}
