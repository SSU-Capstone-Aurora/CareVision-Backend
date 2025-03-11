package aurora.carevisionapiserver.global.elasticsearch.api;

import java.util.Collections;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aurora.carevisionapiserver.global.elasticsearch.service.ElasticSearchSyncService;
import aurora.carevisionapiserver.global.response.BaseResponse;
import aurora.carevisionapiserver.global.response.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "X_ElasticSearch 🔍", description = "ElasticSearch 데이터 동기화 (내부 시스템 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/elasticsearch")
public class ElasticSearchSyncController {

    private final ElasticSearchSyncService elasticSearchSyncService;

    @PostMapping("/patients/sync")
    public BaseResponse<Object> syncPatients() {
        elasticSearchSyncService.syncPatientDatabaseToElasticsearch();
        return BaseResponse.of(SuccessStatus._OK, Collections.emptyMap());
    }

    @PostMapping("/nurses/sync")
    public BaseResponse<Object> syncNurses() {
        elasticSearchSyncService.syncNurseDatabaseToElasticsearch();
        return BaseResponse.of(SuccessStatus._OK, Collections.emptyMap());
    }
}
