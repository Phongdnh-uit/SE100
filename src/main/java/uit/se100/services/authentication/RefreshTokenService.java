package uit.se100.services.authentication;

import uit.se100.entities.authentication.RefreshToken;
import uit.se100.entities.authentication.User;

public interface RefreshTokenService {
  RefreshToken findByToken(String token);

  RefreshToken createRefreshToken(User user);

  void verify(RefreshToken token);

  void delete(RefreshToken token);
}
