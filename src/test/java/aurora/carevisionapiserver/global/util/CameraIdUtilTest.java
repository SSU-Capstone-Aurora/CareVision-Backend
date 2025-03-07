package aurora.carevisionapiserver.global.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CameraIdUtilTest {
    @DisplayName("카메라의 아이디에서 숫자 값을 추출해낸다.")
    @Test
    void parseLongId() {
        // given
        Long LongId = 1001L;
        String cameraId = "CAM" + LongId;

        // when
        Long parsedId = CameraIdUtil.parseLongId(cameraId);

        // then
        assertEquals(parsedId, LongId);
    }
}
