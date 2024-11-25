package aurora.carevisionapiserver.global.auth.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import aurora.carevisionapiserver.global.auth.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    @Transactional
    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUsername(String username);
}
