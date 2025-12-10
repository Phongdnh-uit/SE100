package uit.se100.dtos.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
  @NotBlank private String credentialId;

  @NotBlank private String password;
}
