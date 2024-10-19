package aurora.carevisionapiserver.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import aurora.carevisionapiserver.domain.hospital.api.AdminHospitalController;
import aurora.carevisionapiserver.domain.hospital.dto.response.HospitalResponse.HospitalSearchResponse;
import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;

@WebMvcTest(AdminHospitalController.class)
public class AdminHospitalControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private HospitalService hospitalService;

    @Test
    @WithMockUser
    @DisplayName("병원 검색 성공한다.")
    public void testSearchHospital_Success() throws Exception {
        String hospitalName = "오로라";
        HospitalSearchResponse hospitalSearchResponse =
                HospitalSearchResponse.builder()
                        .name("오로라 병원")
                        .address("우주 정거장")
                        .ykiho("aurora")
                        .build();

        when(hospitalService.searchHospital(hospitalName))
                .thenReturn(Collections.singletonList(hospitalSearchResponse));

        mockMvc.perform(
                        get("/api/admin/hospitals")
                                .param("search", hospitalName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.hospitals[0].name").value("오로라 병원"))
                .andExpect(jsonPath("$.result.hospitals[0].address").value("우주 정거장"))
                .andExpect(jsonPath("$.result.hospitals[0].ykiho").value("aurora"));
    }

    @Test
    @WithMockUser
    @DisplayName("병원 검색 실패한다.")
    public void testSearchHospital_NotFound() throws Exception {
        String hospitalName = "Nonexistent Hospital";

        when(hospitalService.searchHospital(hospitalName))
                .thenThrow(new HospitalException(ErrorStatus.HOSPITAL_NOT_FOUND));

        mockMvc.perform(
                        get("/api/admin/hospitals")
                                .param("search", hospitalName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("HOSPITAL400"));
    }

    @Test
    @WithMockUser
    @DisplayName("병원 과 검색에 성공한다.")
    public void testSearchDepartment_Success() throws Exception {
        String ykiho = "test-department";
        List<String> departments = List.of("내과", "외과");

        when(hospitalService.searchDepartment(ykiho)).thenReturn(departments);

        mockMvc.perform(
                        get("/api/admin/departments")
                                .param("hospital", ykiho)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.departments[0]").value("내과"))
                .andExpect(jsonPath("$.result.departments[1]").value("외과"))
                .andExpect(jsonPath("$.result.totalCount").value(2));
    }
}
