package uit.se100.services.authentication;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uit.se100.entities.authentication.RefreshToken;
import uit.se100.entities.authentication.User;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.repositories.authentication.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

  @Value("${jwt.refresh-token.expiration}")
  private Long expiration;

  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public RefreshToken findByToken(String token) {
    return refreshTokenRepository
        .findOne((root, _, builder) -> builder.equal(root.get("token"), token))
        .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALID));
  }

  @Override
  public RefreshToken createRefreshToken(User user) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setExpiresAt(Instant.now().plusSeconds(expiration));
    refreshToken.setToken(UUID.randomUUID().toString());
    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public void verify(RefreshToken token) {
    if (token.getExpiresAt().isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);
      throw new ApiException(ErrorCode.TOKEN_EXPIRED);
    }
  }

  @Override
  public void delete(RefreshToken token) {
    refreshTokenRepository.delete(token);
  }
}
