package aurora.carevisionapiserver.global.elasticsearch.service;

public interface ElasticSearchSyncService {
    void syncPatientDatabaseToElasticsearch();

    void syncNurseDatabaseToElasticsearch();
}
