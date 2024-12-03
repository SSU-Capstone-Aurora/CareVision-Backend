package aurora.carevisionapiserver.global.fcm.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.fcm.converter.AlarmConverter;
import aurora.carevisionapiserver.global.fcm.converter.ClientTokenConverter;
import aurora.carevisionapiserver.global.fcm.domain.ClientToken;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmInfoListResponse;
import aurora.carevisionapiserver.global.fcm.dto.AlarmResponse.AlarmInfoResponse;
import aurora.carevisionapiserver.global.fcm.dto.FcmClientRequest;
import aurora.carevisionapiserver.global.fcm.dto.FcmResponse.FireStoreResponse;
import aurora.carevisionapiserver.global.fcm.exception.FcmException;
import aurora.carevisionapiserver.global.fcm.repository.ClientTokenRepository;
import aurora.carevisionapiserver.global.fcm.service.FcmService;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.TimeAgoUtil;
import aurora.carevisionapiserver.global.util.TimeConverter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
    private static final String TOKEN_ERROR_MESSAGE = "NotRegistered";
    private final ClientTokenRepository clientTokenRepository;

    @Override
    @Transactional
    public void saveClientToken(FcmClientRequest fcmClientRequest) {
        ClientToken clientToken = ClientTokenConverter.toClientToken(fcmClientRequest);
        clientTokenRepository.save(clientToken);
    }

    @Override
    public String findClientToken(Nurse nurse) {
        return clientTokenRepository
                .findClientTokenByUsername(nurse.getUsername())
                .orElseThrow(() -> new FcmException(ErrorStatus.CLIENT_TOKEN_NOT_FOUND));
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

    private void sendMessageToFcm(Patient patient, String registrationToken, Timestamp time) {
        Message message =
                Message.builder()
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
                        .build();
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
