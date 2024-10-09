package aurora.carevisionapiserver.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import aurora.carevisionapiserver.domain.hospital.api.AdminHospitalController;
import aurora.carevisionapiserver.domain.hospital.dto.HospitalDTO.SearchHospitalDTO;
import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.domain.hospital.service.HospitalService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;

@WebMvcTest(AdminHospitalController.class)
public class AdminHospitalControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private HospitalService hospitalService;

    @Test
    @WithMockUser
    @DisplayName("병원 검색 성공합니다.")
    public void testSearchHospital_Success() throws Exception {
        String hospitalName = "오로라";
        SearchHospitalDTO hospitalDTO =
                SearchHospitalDTO.builder()
                        .name("오로라 병원")
                        .address("우주 정거장")
                        .ykiho("aurora")
                        .build();

        when(hospitalService.searchHospital(hospitalName))
                .thenReturn(Collections.singletonList(hospitalDTO));

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
    @DisplayName("병원 검색 실패합니다.")
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
}
