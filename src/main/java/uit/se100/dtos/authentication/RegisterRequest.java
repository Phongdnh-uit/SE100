package uit.se100.dtos.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import uit.se100.annotations.ValidPhone;
import uit.se100.dtos.Action.Create;

@Getter
@Setter
public class RegisterRequest {
  @NotBlank private String username;

  @NotBlank @Email private String email;

  @NotBlank @ValidPhone private String phone;

  @NotBlank(groups = {Create.class})
  private String password;
}
