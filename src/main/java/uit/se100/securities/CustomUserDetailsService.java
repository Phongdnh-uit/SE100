package uit.se100.securities;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import uit.se100.entities.authentication.User;
import uit.se100.repositories.authentication.UserRepository;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final EmailValidator emailValidator = new EmailValidator();

  @Override
  public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
    boolean isEmail = emailValidator.isValid(credential, null);
    User user = null;
    if (isEmail) {
      user =
          userRepository
              .findOne((root, _, builder) -> builder.equal(root.get("email"), credential))
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    } else {
      user =
          userRepository
              .findOne((root, _, builder) -> builder.equal(root.get("username"), credential))
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    return CustomUserDetails.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .password(user.getPasswordHash())
        .role(user.getRole())
        .authorities(Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
        .build();
  }
}
