package aurora.carevisionapiserver.global.fcm.converter;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmData;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmInfoListResponse;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmInfoResponse;
import aurora.carevisionapiserver.global.fcm.dto.FcmResponse.FireStoreResponse;

public class AlarmConverter {
    public static Map<String, Object> toAlarmData(Patient patient) {
        AlarmData alarmData =
                AlarmData.builder()
                        .patientId(patient.getId())
                        .patientName(patient.getName())
                        .inpatientWardNumber(patient.getBed().getInpatientWardNumber())
                        .patientRoomNumber(patient.getBed().getPatientRoomNumber())
                        .bedNumber(patient.getBed().getBedNumber())
                        .read(false)
                        .build();
        return toMap(alarmData);
    }

    private static Map<String, Object> toMap(AlarmData alarmData) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(alarmData, Map.class);
    }

    public static AlarmInfoResponse toAlarmInfoResponse(
            FireStoreResponse alarmInfo, String timeAgo) {
        return AlarmInfoResponse.builder()
                .documentId(alarmInfo.getDocumentId())
                .patientId(alarmInfo.getPatientId())
                .patientName(alarmInfo.getPatientName())
                .inpatientWardNumber(alarmInfo.getInpatientWardNumber())
                .patientRoomNumber(alarmInfo.getPatientRoomNumber())
                .bedNumber(alarmInfo.getBedNumber())
                .timeAgo(timeAgo)
                .build();
    }

    public static AlarmInfoListResponse toAlarmInfoListResponse(
            List<AlarmInfoResponse> alarmInfoResponse) {
        return AlarmInfoListResponse.builder()
                .alarmInfoList(alarmInfoResponse)
                .totalCount(alarmInfoResponse.size())
                .build();
    }
}
