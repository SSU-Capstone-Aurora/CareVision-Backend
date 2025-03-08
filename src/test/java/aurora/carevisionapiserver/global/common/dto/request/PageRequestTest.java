package aurora.carevisionapiserver.global.common.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PageRequestTest {
    @DisplayName("요청된 size가 null일 경우 size는 8로 설정된다.")
    @Test
    void PageRequest() {
        // given
        Long lastIdx = 1L;

        // when
        PageRequest request = new PageRequest(lastIdx);

        // then
        assertThat(request.getSize()).isEqualTo(8);
    }
}
