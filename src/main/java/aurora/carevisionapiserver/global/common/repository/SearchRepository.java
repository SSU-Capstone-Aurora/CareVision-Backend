package aurora.carevisionapiserver.global.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

public interface SearchRepository<T> {
    @Query(
            value = "SELECT * FROM #{#entityName.toLowerCase()} e WHERE e.name LIKE %?1%",
            nativeQuery = true)
    List<T> searchByName(String name);
}
