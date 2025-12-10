package uit.se100.repositories.authentication;

import org.springframework.stereotype.Repository;
import uit.se100.entities.authentication.User;
import uit.se100.repositories.SimpleRepository;

@Repository
public interface UserRepository extends SimpleRepository<User, Long> {}
