package aurora.carevisionapiserver.service;

import static aurora.carevisionapiserver.domain.hospital.domain.Department.SURGERY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.Impl.NurseServiceImpl;

@ExtendWith(MockitoExtension.class)
public class NurseServiceTest {
    @InjectMocks private NurseServiceImpl nurseService;
    @Mock private NurseRepository nurseRepository;

    private Nurse createNurse() {
        Hospital hospital = Hospital.builder().id(1L).name("서울병원").department(SURGERY).build();

        String dateTime = "2024-10-11 17:57:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Nurse.builder()
                .id(1L)
                .name("김간호사")
                .username("kim1")
                .registeredAt(LocalDateTime.parse(dateTime, formatter))
                .hospital(hospital)
                .build();
    }

    private Nurse createOtherNurse() {
        Hospital hospital = Hospital.builder().id(2L).name("대구병원").department(SURGERY).build();

        String dateTime = "2024-09-10 17:57:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Nurse.builder()
                .id(2L)
                .name("최간호사")
                .username("choi2")
                .registeredAt(LocalDateTime.parse(dateTime, formatter))
                .hospital(hospital)
                .build();
    }

    @Test
    @DisplayName("간호사 찾기 성공한다.")
    void getNurseListSuccess() {
        // given
        List<Nurse> nurses = List.of(createOtherNurse(), createNurse());
        given(nurseRepository.findAll(Sort.by(Sort.Direction.DESC, "registeredAt")))
                .willReturn(nurses);
        // when
        List<Nurse> result = nurseService.getNurseList();
        // then
        assertEquals(nurses.size(), result.size());
        assertEquals(nurses.get(0).getId(), result.get(0).getId());
        assertEquals(nurses.get(0).getName(), result.get(0).getName());
        assertEquals(nurses.get(1).getId(), result.get(1).getId());
        assertEquals(nurses.get(1).getName(), result.get(1).getName());
    }

    @Test
    @DisplayName("간호사가 존재하는지 조회한다.")
    void existsNurseTest() {
        // given
        Nurse nurse = createNurse();
        given(nurseRepository.findById(nurse.getId())).willReturn(Optional.of(nurse));

        // when
        Optional<Nurse> result = nurseService.getNurse(nurse.getId());

        // then
        assertTrue(result.isPresent());
        assertEquals(nurse.getId(), result.get().getId());
        assertEquals(nurse.getName(), result.get().getName());
    }

    @Test
    @DisplayName("간호사 검색에 성공한다.")
    void searchNurseSuccess() {
        // given
        String nurseName = "test";
        List<Nurse> nurses = List.of(createNurse(), createOtherNurse());
        given(nurseRepository.searchByName(nurseName)).willReturn(nurses);

        // when
        List<Nurse> result = nurseService.searchNurse(nurseName);

        // then
        assertEquals(nurses.size(), result.size());
        assertEquals(nurses.get(0).getName(), result.get(0).getName());
        assertEquals(nurses.get(0).getId(), result.get(0).getId());
        assertEquals(nurses.get(1).getName(), result.get(1).getName());
        assertEquals(nurses.get(1).getId(), result.get(1).getId());
    }
}
