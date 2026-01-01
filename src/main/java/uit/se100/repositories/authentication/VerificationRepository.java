package uit.se100.repositories.authentication;

import org.springframework.stereotype.Repository;
import uit.se100.entities.authentication.Verification;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface VerificationRepository extends SimpleRepository<Verification, Long> {}
