package aurora.carevisionapiserver.global.fcm.converter;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmData;

public class AlarmConverter {
    public static Map<String, Object> toAlarmData(Patient patient) {
        AlarmData alarmData =
                AlarmData.builder()
                        .patientId(patient.getId())
                        .patientName(patient.getName())
                        .inpatientWardNumber(patient.getBed().getInpatientWardNumber())
                        .patientRoomNumber(patient.getBed().getPatientRoomNumber())
                        .bedNumber(patient.getBed().getBedNumber())
                        .build();
        return toMap(alarmData);
    }

    private static Map<String, Object> toMap(AlarmData alarmData) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(alarmData, Map.class);
    }
}
