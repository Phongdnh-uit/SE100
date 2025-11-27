package uit.se100.securities.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import uit.se100.entities.authentication.User;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.securities.CustomUserDetails;

@RequiredArgsConstructor
@Component
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  private final UserRepository userRepository;
  private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<? extends GrantedAuthority> jwtAuthorities =
        jwtGrantedAuthoritiesConverter.convert(jwt);
    Set<? extends GrantedAuthority> authorities =
        jwtAuthorities == null
            ? Collections.emptySet()
            : Collections.unmodifiableSet(Set.copyOf(jwtAuthorities));

    Long userId = null;
    try {
      userId = Long.parseLong(jwt.getSubject());
    } catch (NumberFormatException e) {
      throw new JwtException("Invalid user ID in JWT subject", e);
    }
    User user =
        userRepository.findById(userId).orElseThrow(() -> new JwtException("User not found"));

    CustomUserDetails principal =
        CustomUserDetails.builder()
            .id(user.getId())
            .email(user.getEmail())
            .phone(user.getPhone())
            .password(user.getPasswordHash())
            .role(user.getRole())
            .authorities(authorities)
            .build();
    return new UsernamePasswordAuthenticationToken(principal, jwt, principal.getAuthorities());
  }
}
