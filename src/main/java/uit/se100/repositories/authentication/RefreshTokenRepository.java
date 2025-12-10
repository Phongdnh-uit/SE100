package uit.se100.repositories.authentication;

import org.springframework.stereotype.Repository;
import uit.se100.entities.authentication.RefreshToken;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface RefreshTokenRepository extends SimpleRepository<RefreshToken, Long> {}
