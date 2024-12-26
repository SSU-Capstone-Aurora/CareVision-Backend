package aurora.carevisionapiserver.global.fcm.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import aurora.carevisionapiserver.domain.camera.converter.CameraConverter;
import aurora.carevisionapiserver.domain.camera.dto.response.CameraResponse.StreamingInfoResponse;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.fcm.converter.AlarmConverter;
import aurora.carevisionapiserver.global.fcm.dto.request.FcmRequest.ClientInfo;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmPreviewResponse;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmResponse.AlarmInfoListResponse;
import aurora.carevisionapiserver.global.fcm.dto.response.AlarmResponse.AlarmInfoResponse;
import aurora.carevisionapiserver.global.fcm.dto.response.FcmResponse.FireStoreResponse;
import aurora.carevisionapiserver.global.fcm.exception.FcmException;
import aurora.carevisionapiserver.global.fcm.service.FcmService;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.TimeAgoUtil;
import aurora.carevisionapiserver.global.util.TimeConverter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
    private static final String TOKEN_ERROR_MESSAGE = "NotRegistered";
    private static final String CLIENT_TOKEN_KEY = "client";
    private final PatientService patientService;
    private final CameraService cameraService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public void saveClientToken(ClientInfo clientInfo) {
        redisTemplate
                .opsForHash()
                .put(CLIENT_TOKEN_KEY, clientInfo.getUsername(), clientInfo.getClientToken());
    }

    @Override
    public String findClientToken(Nurse nurse) {
        return (String) redisTemplate.opsForHash().get(CLIENT_TOKEN_KEY, nurse.getUsername());
    }

    @Override
    public void abnormalBehaviorAlarm(Patient patient, String registrationToken) {
        Timestamp time = Timestamp.now();

        sendMessageToFcm(patient, registrationToken, time);
        saveMassageToFireStore(patient, time);
    }

    @Override
    public AlarmInfoListResponse getAlarmsInfo(Nurse nurse) {
        CollectionReference alarmsCollection = getAlarmCollection(nurse.getId().toString());

        List<FireStoreResponse> fireStoreResponses = fetchFireStoreData(alarmsCollection);
        List<AlarmInfoResponse> alarmInfoList = convertToAlarmInfoList(fireStoreResponses);

        return AlarmConverter.toAlarmInfoListResponse(alarmInfoList);
    }

    @Override
    public StreamingInfoResponse getAlarmInfo(Nurse nurse, String documentId) {
        DocumentSnapshot alarm;
        try {
            alarm = getAlarmCollection(nurse.getId().toString()).document(documentId).get().get();
        } catch (Exception e) {
            throw new FcmException(ErrorStatus.EXECUTION_FAILED);
        }

        Map<String, Object> data = alarm.getData();
        if (data.containsKey("read")) {
            boolean isReadValue = (boolean) data.get("read");
            if (!isReadValue) {
                try {
                    getAlarmCollection(nurse.getId().toString())
                            .document(documentId)
                            .update("read", true)
                            .get();
                } catch (Exception e) {
                    throw new FcmException(ErrorStatus.EXECUTION_FAILED);
                }
            }
        }
        Long patientId = (Long) data.get("patientId");

        Patient patient = patientService.getPatient(patientId);
        String cameraUrl = cameraService.getStreamingUrl(patient);
        return CameraConverter.toStreamingInfoResponse(cameraUrl, patient);
    }

    @Override
    public AlarmPreviewResponse getAlarmCount(Nurse nurse) {
        CollectionReference alarmsCollection = getAlarmCollection(nurse.getId().toString());
        long count = countFireStoreData(alarmsCollection);

        return AlarmPreviewResponse.of(count);
    }

    private long countFireStoreData(CollectionReference alarmsCollection) {
        Query query = alarmsCollection.whereEqualTo("read", false);

        try {
            QuerySnapshot querySnapshot = query.get().get();

            return querySnapshot.size();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToFcm(Patient patient, String registrationToken, Timestamp time) {
        Message message = AlarmConverter.toMessage(patient, time, registrationToken);
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessage().contains(TOKEN_ERROR_MESSAGE)) {
                throw new FcmException(ErrorStatus.CLIENT_TOKEN_EXPIRED);
            }
        }
    }

    private void saveMassageToFireStore(Patient patient, Timestamp time) {
        Map<String, Object> data = createAlarmData(patient, time);
        saveToFirestore(data, patient.getNurse().getId().toString());
    }

    private Map<String, Object> createAlarmData(Patient patient, Timestamp time) {
        Map<String, Object> alarmData = AlarmConverter.toAlarmData(patient);
        alarmData.put("time", time);
        return alarmData;
    }

    private void saveToFirestore(Map<String, Object> data, String nurseId) {
        CollectionReference alarmsCollection = getAlarmCollection(nurseId);
        alarmsCollection.add(data);
    }

    private CollectionReference getAlarmCollection(String nurseId) {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("users").document(nurseId).collection("alarms");
    }

    private List<FireStoreResponse> fetchFireStoreData(CollectionReference alarmsCollection) {
        Query query = alarmsCollection.orderBy("time", Query.Direction.DESCENDING);
        List<FireStoreResponse> responses;

        try {
            ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();
            QuerySnapshot querySnapshot = querySnapshotFuture.get();
            responses = extractFireStoreData(querySnapshot);
        } catch (InterruptedException | ExecutionException e) {
            throw new FcmException(ErrorStatus.EXECUTION_FAILED);
        }

        return responses;
    }

    private List<FireStoreResponse> extractFireStoreData(QuerySnapshot querySnapshot) {
        List<FireStoreResponse> responses = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            FireStoreResponse fireStoreInfo = document.toObject(FireStoreResponse.class);
            fireStoreInfo.setDocumentId(document.getId());
            responses.add(fireStoreInfo);
        }

        return responses;
    }

    private List<AlarmInfoResponse> convertToAlarmInfoList(
            List<FireStoreResponse> fireStoreResponses) {
        List<AlarmInfoResponse> alarmsInfo = new ArrayList<>();

        for (FireStoreResponse fireStoreInfo : fireStoreResponses) {
            String timeAgoMessage = generateTimeAgoMessage(fireStoreInfo.getTime());
            AlarmInfoResponse alarmInfo =
                    AlarmConverter.toAlarmInfoResponse(fireStoreInfo, timeAgoMessage);
            alarmsInfo.add(alarmInfo);
        }

        return alarmsInfo;
    }

    private String generateTimeAgoMessage(Timestamp timestamp) {
        LocalDateTime time = TimeConverter.convertTimestampToLocalDateTime(timestamp);
        return TimeAgoUtil.getTimeAgoMessage(time);
    }
}
