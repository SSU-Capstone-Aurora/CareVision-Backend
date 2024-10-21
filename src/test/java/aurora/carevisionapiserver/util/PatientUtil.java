package aurora.carevisionapiserver.util;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.patient.domain.Patient;

public class PatientUtil {
    public static Patient createPatient() {
        Bed bed =
                Bed.builder()
                        .id(1L)
                        .inpatientWardNumber(1L)
                        .patientRoomNumber(2L)
                        .bedNumber(3L)
                        .build();
        return Patient.builder().id(1L).name("test").code("kk-123").bed(bed).build();
    }

    public static Patient createOtherPatient() {
        Bed bed =
                Bed.builder()
                        .id(2L)
                        .inpatientWardNumber(2L)
                        .patientRoomNumber(3L)
                        .bedNumber(3L)
                        .build();
        return Patient.builder().id(2L).name("kangrok").code("rr-123").bed(bed).build();
    }
}
