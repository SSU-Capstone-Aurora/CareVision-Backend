package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import aurora.carevisionapiserver.domain.nurse.domain.NurseDocument;

public interface NurseEsRepository extends ElasticsearchRepository<NurseDocument, String> {

    List<NurseDocument> findByIsActivatedTrueAndDepartmentIdAndNurseIdIsGreaterThanOrderByNurseId(
            Long departmentId, Long lastIdx);

    List<NurseDocument>
            findByIsActivatedTrueAndNameAndDepartmentIdAndNurseIdIsGreaterThanOrderByNurseId(
                    String name, Long departmentId, Long lastIdx);
}
