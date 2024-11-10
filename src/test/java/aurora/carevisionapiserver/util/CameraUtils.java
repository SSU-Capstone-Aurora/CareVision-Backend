package aurora.carevisionapiserver.util;

import aurora.carevisionapiserver.domain.camera.dto.request.CameraRequest.CameraSelectRequest;

public class CameraUtils {
    public static CameraSelectRequest createCameraSelectRequest() {
        return CameraSelectRequest.builder().id("1").build();
    }
}
