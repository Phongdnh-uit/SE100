package uit.se100.securities;

import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uit.se100.enums.RoleEnum;

@Builder
@Getter
public class CustomUserDetails implements UserDetails {
  private Long id;
  private String password;
  private String username;
  private String email;
  private String phone;
  private RoleEnum role;

  private final Set<? extends GrantedAuthority> authorities;

  private final Map<String, Object> attributes;

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
