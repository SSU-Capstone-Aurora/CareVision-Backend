package aurora.carevisionapiserver.global.fcm.converter;

import java.util.List;

import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmInfoListResponse;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmInfoResponse;
import aurora.carevisionapiserver.global.fcm.dto.FcmResponse.FireStoreResponse;

public class AlarmConverter {
    public static AlarmInfoResponse toAlarmInfoResponse(
            FireStoreResponse alarmInfo, String timeAgo) {
        return AlarmInfoResponse.builder()
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
