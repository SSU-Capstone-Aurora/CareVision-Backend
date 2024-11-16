package aurora.carevisionapiserver.domain.camera.dto.response;

public record CameraUrl(String url) {
    public static CameraUrl from(String url) {
        return new CameraUrl(url);
    }
}
