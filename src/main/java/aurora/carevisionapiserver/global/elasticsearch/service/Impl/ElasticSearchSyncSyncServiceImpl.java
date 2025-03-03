package aurora.carevisionapiserver.global.elasticsearch.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aurora.carevisionapiserver.domain.nurse.converter.NurseDocumentConverter;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;
import aurora.carevisionapiserver.domain.nurse.repository.NurseEsRepository;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.patient.converter.PatientDocumentConverter;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;
import aurora.carevisionapiserver.domain.patient.repository.PatientEsRepository;
import aurora.carevisionapiserver.domain.patient.repository.PatientRepository;
import aurora.carevisionapiserver.global.elasticsearch.service.ElasticSearchSyncService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ElasticSearchSyncSyncServiceImpl implements ElasticSearchSyncService {

    private final PatientRepository patientRepository;
    private final PatientEsRepository patientESRepository;

    private final NurseRepository nurseRepository;
    private final NurseEsRepository nurseEsRepository;

    @Transactional
    public void syncPatientDatabaseToElasticsearch() {
        patientESRepository.deleteAll();
        List<Patient> patients = patientRepository.findAll();
        List<PatientDocument> esDocuments =
                PatientDocumentConverter.toPatientDocumentList(patients);
        patientESRepository.saveAll(esDocuments);
    }

    @Transactional
    public void syncNurseDatabaseToElasticsearch() {
        nurseEsRepository.deleteAll();
        List<Nurse> nurses = nurseRepository.findAll();
        List<NurseDocument> esDocuments = NurseDocumentConverter.toNurseDocumentList(nurses);
        nurseEsRepository.saveAll(esDocuments);
    }
}
