package uit.se100.dtos.authentication;

import lombok.Getter;
import lombok.Setter;
import uit.se100.dtos.user.UserResponse;

@Getter
@Setter
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private UserResponse user;
}
