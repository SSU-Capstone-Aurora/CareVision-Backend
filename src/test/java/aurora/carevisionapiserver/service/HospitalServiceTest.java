package aurora.carevisionapiserver.service;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;
import aurora.carevisionapiserver.domain.hospital.service.Impl.HospitalServiceImpl;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {
    @InjectMocks private HospitalServiceImpl hospitalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("조회된 병원을 파싱한다.")
    void parseHospitalTest() throws IOException {
        // Given
        StringBuilder mockApiResponse =
                new StringBuilder(
                        "{"
                                + "\"response\": {"
                                + "\"header\": {"
                                + "\"resultCode\": \"00\","
                                + "\"resultMsg\": \"NORMAL SERVICE.\""
                                + "},"
                                + "\"body\": {"
                                + "\"items\": {"
                                + "\"item\": [{"
                                + "\"addr\": \"우주 정거장\","
                                + "\"yadmNm\": \"오로라 병원\","
                                + "\"ykiho\": \"aurora\""
                                + "}]"
                                + "},"
                                + "\"totalCount\": 1"
                                + "}"
                                + "}"
                                + "}");

        // when
        List<SearchHospitalDTO> hospitalDTOList =
                hospitalService.parseHospitalInfo(mockApiResponse);

        // then
        assertThat(hospitalDTOList).isNotNull();
        assertThat(hospitalDTOList.size()).isEqualTo(1);

        SearchHospitalDTO hospitalDTO = hospitalDTOList.get(0);
        assertThat(hospitalDTO.getName()).isEqualTo("오로라 병원");
        assertThat(hospitalDTO.getAddress()).isEqualTo("우주 정거장");
        assertThat(hospitalDTO.getYkiho()).isEqualTo("aurora");
    }
}
