package aurora.carevisionapiserver.global.common.dto.request;

public final class PageForCameraRequest extends BasePageRequest<String> {
    public PageForCameraRequest(String cameraId) {
        super(cameraId);
    }

    public PageForCameraRequest(String cameraId, int size) {
        super(cameraId, size);
    }

    public String getCameraId() {
        return super.identifier;
    }
}
