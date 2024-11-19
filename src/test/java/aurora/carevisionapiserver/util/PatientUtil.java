package aurora.carevisionapiserver.util;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.dto.request.PatientRequest.PatientCreateRequest;

public class PatientUtil {
    public static Patient createPatient() {
        Bed bed = BedUtils.createBed();
        return Patient.builder().id(1L).name("test").code("kk-123").bed(bed).build();
    }

    public static Patient createOtherPatient() {
        Bed bed = BedUtils.createBed();
        return Patient.builder().id(2L).name("kangrok").code("rr-123").bed(bed).build();
    }

    public static PatientCreateRequest createPatientCreateRequest() {
        return PatientCreateRequest.builder()
                .name("test")
                .code("kk-123")
                .bed("100동 200호 30번")
                .build();
    }

    public static PatientCreateRequest createPatientCreateRequestWithInvalidBedInfo() {
        return PatientCreateRequest.builder()
                .name("test")
                .code("kk-123")
                .bed("잘못된 형식의 베드 번호")
                .build();
    }
}
