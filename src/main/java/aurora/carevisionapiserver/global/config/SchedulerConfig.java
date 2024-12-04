package aurora.carevisionapiserver.global.config;

import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.domain.camera.service.RtspService;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.global.infra.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    private static final int DAYS_OLD = 1;
    private final PatientRepository patientRepository;
    private final RtspService rtspService;
    private final S3Service s3Service;

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다 수행
    private void updateThumbnail() {
        log.info("썸네일 업데이트 시작");

        List<Patient> patients = patientRepository.findAll();

        for (Patient patient : patients) {
            Boolean response = rtspService.requestThumbnailUrl(patient);
            log.info("환자 아이디: " + patient.getId() + " 썸네일 업데이트 결과: " + response);
        }

        log.info("썸네일 업데이트 끝");
    }

    @Scheduled(cron = "0 0 9,18 * * *") //  오전 9시와 오후 6시에 수행
    private void deleteOldThumbnail() {
        log.info("오래된 썸네일 삭제 시작");

        List<Patient> patients = patientRepository.findAll();

        for (Patient patient : patients) {
            s3Service.deleteOldThumbnail(patient.getId(), DAYS_OLD);
        }

        log.info("오래된 썸네일 삭제 끝");
    }
}
