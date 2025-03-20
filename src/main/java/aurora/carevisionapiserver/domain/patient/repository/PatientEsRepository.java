package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import aurora.carevisionapiserver.domain.patient.domain.PatientDocument;

public interface PatientEsRepository extends ElasticsearchRepository<PatientDocument, String> {
    @Query("{\"match\": {\"name\": \"?0\"}}") // TODO : 이 메서드 사용하는 API 통합할 때 ES 쿼리 메서드로 변경
    List<PatientDocument> searchByName(String name);

    @Query("{\"bool\": {\"must\": [ {\"term\": {\"nurseId\": -1}} ] }}")
    List<PatientDocument> findAllByNurseIdIsNull();

    @Query(
            "{\"bool\": {\"must\": [ {\"match\": {\"name\": \"?0\"}}, {\"term\": {\"nurseId\": -1}} ] }}")
    List<PatientDocument> findByNameAndNurseIdIsNull(String name);
}
