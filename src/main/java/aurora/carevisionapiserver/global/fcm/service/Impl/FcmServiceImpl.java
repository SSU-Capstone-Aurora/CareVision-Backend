package aurora.carevisionapiserver.global.fcm.service.Impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.fcm.converter.ClientTokenConverter;
import aurora.carevisionapiserver.global.fcm.domain.ClientToken;
import aurora.carevisionapiserver.global.fcm.dto.FcmClientRequest;
import aurora.carevisionapiserver.global.fcm.exception.FcmException;
import aurora.carevisionapiserver.global.fcm.repository.ClientTokenRepository;
import aurora.carevisionapiserver.global.fcm.service.FcmService;
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
                .orElseThrow(() -> new FcmException(ErrorStatus.CLIENT_TOKEN_NOTFOUND));
    }

    @Override
    public void abnormalBehaviorAlarm(Patient patient, Long nurseId, String registrationToken) {
        String time = LocalDateTime.now().toString();

        sendMessageToFcm(patient, registrationToken, time);
        saveMassageToFireStore(patient, nurseId, time);
    }

    private void sendMessageToFcm(Patient patient, String registrationToken, String time) {
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
                        .putData("time", time)
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

    private void saveMassageToFireStore(Patient patient, Long nurseId, String time) {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, String> data = new HashMap<>();
        data.put("patientName", patient.getName());
        data.put("inpatientWardNumber", String.valueOf(patient.getBed().getInpatientWardNumber()));
        data.put("patientRoomNumber", String.valueOf(patient.getBed().getPatientRoomNumber()));
        data.put("bedNumber", String.valueOf(patient.getBed().getBedNumber()));
        data.put("time", time);

        db.collection("cv").document(nurseId.toString()).set(data);
    }
}
