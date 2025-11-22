package uit.se100.securities;

import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Builder
@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {
  private Long id;
  private String password;
  private String email;
  private String phone;
  private Long roleId;

  private final Set<? extends GrantedAuthority> authorities;

  private final Map<String, Object> attributes;

  @Override
  public String getName() {
    return String.valueOf(id);
  }

  @Override
  public String getUsername() {
    return String.valueOf(id);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
