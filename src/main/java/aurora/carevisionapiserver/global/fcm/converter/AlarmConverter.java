package aurora.carevisionapiserver.global.fcm.converter;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.Message;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmResponse.AlarmData;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmResponse.AlarmInfoListResponse;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmResponse.AlarmInfoResponse;
import aurora.carevisionapiserver.global.fcm.dto.response.FcmResponse.FireStoreResponse;

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

    public static Message toMessage(Patient patient, Timestamp time, String registrationToken) {
        return Message.builder()
                .putData("bedNumber", String.valueOf(patient.getBed().getBedNumber()))
                .putData(
                        "inpatientWardNumber",
                        String.valueOf(patient.getBed().getInpatientWardNumber()))
                .putData(
                        "patientRoomNumber",
                        String.valueOf(patient.getBed().getPatientRoomNumber()))
                .putData("patientName", patient.getName())
                .putData("patientId", patient.getId().toString())
                .putData("time", time.toString())
                .putData("read", "false")
                .setToken(registrationToken)
                .setAndroidConfig(
                        AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                .build();
    }
}
