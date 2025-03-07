package aurora.carevisionapiserver.global.common.dto.request;

public record PageForCameraRequest(String cameraId, int size) {
    public PageForCameraRequest(String cameraId, int size) {
        this.cameraId = cameraId;
        this.size = (size > 0) ? size : 8;
    }
}
