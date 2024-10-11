package aurora.carevisionapiserver.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;
import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.domain.hospital.service.Impl.HospitalServiceImpl;
import aurora.carevisionapiserver.global.util.ApiExplorer;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {
    @InjectMocks private HospitalServiceImpl hospitalService;
    @Mock private ApiExplorer explorer;

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

    @Test
    @DisplayName("병원 과를 검색한다.")
    void searchDepartment() throws IOException {
        // Given
        String ykiho = "12345";
        StringBuilder validResponse =
                new StringBuilder(
                        "{ \"response\": { \"body\": { \"totalCount\": 1, \"items\": { \"item\": [{ \"dgsbjtCdNm\": \"내과\" }, { \"dgsbjtCdNm\": \"외과\" }] } } } }");

        when(explorer.callDepartmentAPI(ykiho)).thenReturn(validResponse);

        // When
        List<String> result = hospitalService.searchDepartment(ykiho);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains("내과"));
        assertTrue(result.contains("외과"));
    }

    @Test
    @DisplayName("과를 검색할 때 검색 결과가 없으면 예외를 던진다.")
    void searchDepartment_withNoDepartments_throwException() throws IOException {
        // Given
        String ykiho = "12345";
        StringBuilder noDepartmentsResponse =
                new StringBuilder("{ \"response\": { \"body\": { \"totalCount\": 0 } } }");

        when(explorer.callDepartmentAPI(ykiho)).thenReturn(noDepartmentsResponse);

        // When & Then
        assertThrows(HospitalException.class, () -> hospitalService.searchDepartment(ykiho));
    }

    @Test
    @DisplayName("과 정보를 파싱한다.")
    void parseDepartmentInfo() throws IOException {
        // Given
        StringBuilder departmentInfo =
                new StringBuilder(
                        "{ \"response\": { \"body\": { \"totalCount\": 1, \"items\": { \"item\": [{ \"dgsbjtCdNm\": \"내과\" }, { \"dgsbjtCdNm\": \"외과\" }] } } } }");

        // When
        List<String> result = hospitalService.parseDepartmentInfo(departmentInfo);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains("내과"));
        assertTrue(result.contains("외과"));
    }

    @Test
    @DisplayName("과 정보를 파싱할 때 검색 결과가 없으면 예외를 던진다.")
    void checkTotalCount_withZero_throwException() throws JsonProcessingException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{ \"response\": { \"body\": { \"totalCount\": 0 } } }";
        JsonNode rootNode = mapper.readTree(jsonString);

        // When & Then
        assertThrows(HospitalException.class, () -> hospitalService.checkTotalCount(rootNode));
    }

    @Test
    @DisplayName("과 정보를 파싱할 때 검색 결과가 있다.")
    void getItemsNode_returnCorrectItemsNode() throws IOException {
        // Given
        String jsonString =
                "{ \"response\": { \"body\": { \"items\": { \"item\": [{ \"dgsbjtCdNm\": \"내과\" }] } } } }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonString);

        // When
        JsonNode itemsNode = hospitalService.getItemsNode(rootNode);

        // Then
        assertTrue(itemsNode.isArray());
        assertEquals(1, itemsNode.size());
        assertEquals("내과", itemsNode.get(0).path("dgsbjtCdNm").asText());
    }
}
