package aurora.carevisionapiserver.domain.patient.exception;

import aurora.carevisionapiserver.global.error.code.BaseErrorCode;
import aurora.carevisionapiserver.global.error.exception.GeneralException;

public class PatientException extends GeneralException {
    public PatientException(BaseErrorCode code) {
        super(code);
    }
}
