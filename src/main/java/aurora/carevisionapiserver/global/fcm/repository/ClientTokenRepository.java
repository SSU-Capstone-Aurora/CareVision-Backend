package aurora.carevisionapiserver.global.fcm.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import aurora.carevisionapiserver.global.fcm.domain.ClientToken;

public interface ClientTokenRepository extends CrudRepository<ClientToken, String> {
    Optional<String> findClientTokenByUsername(String username);
}
