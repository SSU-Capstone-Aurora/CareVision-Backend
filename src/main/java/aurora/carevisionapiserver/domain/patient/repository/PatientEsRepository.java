package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;

public interface PatientEsRepository extends ElasticsearchRepository<PatientDocument, String> {
    @Query("{\"match\": {\"name.ngram\": \"?0\"}}")
    List<PatientDocument> searchByName(String name);

    @Query("{\"bool\": {\"must\": [ {\"term\": {\"nurseId\": -1}} ] }}")
    List<PatientDocument> findAllByNurseIdIsNull();

    @Query(
            "{\"bool\": {\"must\": [ {\"match\": {\"name.ngram\": \"?0\"}}, {\"term\": {\"nurseId\": -1}} ] }}")
    List<PatientDocument> findByNameAndNurseIdIsNull(String name);
}
