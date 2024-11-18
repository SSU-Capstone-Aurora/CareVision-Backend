package aurora.carevisionapiserver.global.fcm.converter;

import java.util.List;
import java.util.Map;

import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse;
import aurora.carevisionapiserver.global.fcm.dto.FcmResponse;
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
    public static AlarmResponse.AlarmInfoResponse toAlarmInfoResponse(
            FcmResponse.FireStoreResponse alarmInfo, String timeAgo) {
        return AlarmResponse.AlarmInfoResponse.builder()
                .patientId(alarmInfo.getPatientId())
                .patientName(alarmInfo.getPatientName())
                .inpatientWardNumber(alarmInfo.getInpatientWardNumber())
                .patientRoomNumber(alarmInfo.getPatientRoomNumber())
                .bedNumber(alarmInfo.getBedNumber())
                .timeAgo(timeAgo)
                .build();
    }

    public static AlarmResponse.AlarmInfoListResponse toAlarmInfoListResponse(
            List<AlarmResponse.AlarmInfoResponse> alarmInfoResponse) {
        return AlarmResponse.AlarmInfoListResponse.builder()
                .alarmInfoList(alarmInfoResponse)
                .totalCount(alarmInfoResponse.size())
                .build();
    }
}
