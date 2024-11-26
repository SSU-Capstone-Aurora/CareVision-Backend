package aurora.carevisionapiserver.global.exception;

import aurora.carevisionapiserver.global.response.code.BaseErrorCode;
import aurora.carevisionapiserver.global.response.exception.GeneralException;

public class S3Exception extends GeneralException {
    public S3Exception(BaseErrorCode code) {
        super(code);
    }
}
