package aurora.carevisionapiserver.global.util;

public class CameraIdUtil {
    public static Long parseLongId(String id) {
        return Long.parseLong(id.replaceAll("[^0-9]", ""));
    }
}
