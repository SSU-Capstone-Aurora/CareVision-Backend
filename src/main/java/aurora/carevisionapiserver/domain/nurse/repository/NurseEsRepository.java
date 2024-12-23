package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;

public interface NurseEsRepository extends ElasticsearchRepository<NurseDocument, String> {
    @Query("{\"match\": {\"name.ngram\": \"?0\"}}")
    List<NurseDocument> findAllByName(String name);
}
