package aurora.carevisionapiserver.global.fcm.service;

import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmInfoListResponse;
import aurora.carevisionapiserver.global.fcm.dto.FcmRequest.ClientInfo;

public interface FcmService {
    void saveClientToken(ClientInfo clientInfo);

    String findClientToken(Nurse nurse);

    void abnormalBehaviorAlarm(Patient patient, String registrationToken);

    AlarmInfoListResponse getAlarmsInfo(Nurse nurse);

    StreamingInfoResponse getAlarmInfo(Nurse nurse, String documentId);
}
