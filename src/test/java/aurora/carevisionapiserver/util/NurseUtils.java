package aurora.carevisionapiserver.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public class NurseUtils {

    public static Nurse createNurse() {
        Hospital hospital = Hospital.builder().id(1L).name("서울병원").department("성형외과").build();
        Patient patient = PatientUtil.createPatient();
        String dateTime = "2024-10-11 17:57:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Nurse.builder()
                .id(1L)
                .name("김간호사")
                .username("kim1")
                .registeredAt(LocalDateTime.parse(dateTime, formatter))
                .hospital(hospital)
                .patients(List.of(patient))
                .build();
    }

    public static Nurse createOtherNurse() {
        Hospital hospital = Hospital.builder().id(2L).name("대구병원").department("성형외과").build();

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
}
