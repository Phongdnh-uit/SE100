package uit.se100.securities;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import uit.se100.configs.JwtConfig;
import uit.se100.entities.authentication.User;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;

@RequiredArgsConstructor
@Component
public class TokenProvider {

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  @Value("${jwt.access-token.expiration}")
  private Long expirationTime;

  public String generateAccessToken(User user) {
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .subject(String.valueOf(user.getId()))
            .claim("username", user.getUsername())
            .claim("role", user.getRole())
            .claim("email", user.getEmail())
            .expiresAt(now.plusSeconds(expirationTime))
            .build();
    JwsHeader jwsHeader = JwsHeader.with(JwtConfig.JWT_ALGORITHM).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  public Jwt validateJwt(String token) {
    try {
      Jwt jwt = jwtDecoder.decode(token);
      if (jwt.getExpiresAt().isBefore(Instant.now())) {
        throw new ApiException(ErrorCode.TOKEN_EXPIRED);
      }
      return jwt;
    } catch (JwtException e) {
      throw new ApiException(ErrorCode.TOKEN_INVALID);
    }
  }
}
