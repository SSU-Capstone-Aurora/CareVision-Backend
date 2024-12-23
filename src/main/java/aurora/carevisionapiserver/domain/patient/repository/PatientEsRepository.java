package aurora.carevisionapiserver.domain.patient.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;

@Repository
public interface PatientEsRepository extends ElasticsearchRepository<PatientDocument, String> {}
